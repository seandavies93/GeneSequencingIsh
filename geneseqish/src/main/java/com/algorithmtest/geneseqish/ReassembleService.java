package com.algorithmtest.geneseqish;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;

@Component 
public class ReassembleService {

    public String reassemble(String entity) {
        Set<String> fragments = new HashSet<>(Arrays.asList(entity.split(";")));

        /* 
            This loop greedily merges pair in order of the largest overlap and then returns a list of those merged pairs
            Repeats until there is one item or no merge occurs
        */
        int previous = fragments.size();
        final HashMap<StringPair, OverlapDetails> pairToOverlapDetails = generatePairsOfFragmentsInBothOrdersAndMergeData(fragments);
        while (fragments.size() > 1) {
            fragments = mergeGreedyPair(fragments, pairToOverlapDetails);
            if (fragments.size() == previous) { // No merges occurred so we are stuck, return the best effort solution
                break;
            }
            previous = fragments.size();
        }
        return String.join(";", fragments);
    }

    private Set<String> mergeGreedyPair(final Set<String> fragments, final HashMap<StringPair, OverlapDetails> pairToOverlapDetails) {
        int maxOverlap = 0;
        Entry<StringPair, OverlapDetails> greedyChoice = null;

        // Find the first maximum overlap
        for (Entry<StringPair, OverlapDetails> entry : pairToOverlapDetails.entrySet()) {
            if (entry.getValue().overlapAmount() > maxOverlap) {
                maxOverlap = entry.getValue().overlapAmount();
                greedyChoice = entry;
            }
        }

        if (greedyChoice == null) return fragments; 

        String first = greedyChoice.getKey().first();
        String second = greedyChoice.getKey().second();
        String merged = greedyChoice.getValue().mergedString();

        // If it is a containment overlap, then the contained string is removed from the list, as well as pair overlap details in the map
        if (first.contains(second)) {
            removePairsWithGivenString(pairToOverlapDetails, second);
            fragments.remove(second);
            return fragments;
        }

        if (second.contains(first)) {
            removePairsWithGivenString(pairToOverlapDetails, first);
            fragments.remove(first);
            return fragments;
        }

        // Remove the two strings of the pair to be merged
        fragments.remove(first);
        fragments.remove(second);

        // Remove stale pairs that included either one of the two strings in the greedy merge pair
        Set<StringPair> toRemove = new HashSet<>();
        for (StringPair pair : pairToOverlapDetails.keySet()) {
            if (pair.first().equals(first) || pair.first().equals(second) ||
                pair.second().equals(first) || pair.second().equals(second)) {
                toRemove.add(pair);
            }
        }
        
        for (StringPair pair : toRemove) {
            pairToOverlapDetails.remove(pair);
        }

        // Calculate the new overlap details for the merged pair
        for (String remaining : fragments) {
            OverlapDetails mergeDetails = findSuffixPrefixOrContainmentOverlap(remaining, merged);
            if (mergeDetails.overlapAmount() > 0) {
                pairToOverlapDetails.put(new StringPair(remaining, merged), mergeDetails);
            }

            mergeDetails = findSuffixPrefixOrContainmentOverlap(merged, remaining);
            if (mergeDetails.overlapAmount() > 0) {
                pairToOverlapDetails.put(new StringPair(merged, remaining), mergeDetails);
            }
        }

        // Add the merged pair into the list
        fragments.add(merged);

        return fragments;
    }

    private void removePairsWithGivenString(final HashMap<StringPair, OverlapDetails> pairToOverlapDetails, String fragment) {
        Set<StringPair> toRemove = new HashSet<>();
        for (StringPair pair : pairToOverlapDetails.keySet()) {

            if (pair.first().equals(fragment)) {
                    toRemove.add(pair);
            }

            if (pair.second().equals(fragment)) {
                    toRemove.add(pair);
            }
        }

        for (StringPair pair : toRemove) {
            pairToOverlapDetails.remove(pair);
        }
    }

    private HashMap<StringPair, OverlapDetails> generatePairsOfFragmentsInBothOrdersAndMergeData(final Set<String> toPairwiseMerge) {
        final HashMap<StringPair, OverlapDetails> pairToOverlapDetails = new HashMap<>();

        // Finds all pairwise merges, recording the indices of the chunks merge as a pair, the size of the overlap, and the corresponding merged string (of the relevant pair)
        for (String item1 : toPairwiseMerge) {
            for (String item2 : toPairwiseMerge) {
                if (item1.equals(item2)) continue; 
                OverlapDetails potentialMerge = findSuffixPrefixOrContainmentOverlap(item1, item2);
                if (potentialMerge.overlapAmount() > 0) {
                    pairToOverlapDetails.put(new StringPair(item1, item2), potentialMerge);
                }
            }
        }
        return pairToOverlapDetails;
    }

    /* 
        Starts with the largest suffix, and progressively reduces its size
        Checks the suffix is the same as the second strings prefix i.e. overlap checking
        Returns the first match, which is the largest due to the iteration order
    */
    private OverlapDetails findSuffixPrefixOrContainmentOverlap(String first, String second) {
        if (second.contains(first)) {
            return new OverlapDetails(first.length(), second);
        }

        for (int i = first.length(); i >= 1; i--) {
            final String suffixOfFirst = first.substring(first.length() - i);
            if (second.startsWith(suffixOfFirst)) {
                return new OverlapDetails(suffixOfFirst.length(), first.substring(0, first.length() - i) + second);
            }
        }

        return new OverlapDetails(0, "");
    }
    
}
