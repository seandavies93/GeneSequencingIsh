package com.algorithmtest.geneseqish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;

@Component 
public class ReassembleService {

    public String reassemble(String entity) {
        List<String> fragments = Arrays.asList(entity.split(";"));

        /* 
            This loop greedily merges pair in order of the largest overlap and then returns a list of those merged pairs
            Repeats until there is one item or no merge occurs
        */
        int previous = fragments.size();
        while (fragments.size() > 1) {
            fragments = mergePairsGreedily(fragments);
            if (fragments.size() == previous) { // No merges occurred so we are stuck, return the best effort solution
                break;
            }
            previous = fragments.size();
        }
        return String.join(";", fragments);
    }

    private List<String> mergePairsGreedily(final List<String> toPairwiseMerge) {
        final HashMap<IndicesOfPair, OverlapDetails> pairIndicesAndOverlapDetails = generatePairsOfFragmentsInBothOrdersAndMergeData(toPairwiseMerge);

        /* 
            The following section merges pairs, prioritising those with the largest overlap. Once two items have been merged they are recorded
            as being merged and can't be merged with another fragment. If there are any fragments left 
            that have not been merged, they are added into the list as well.
        */
        final Set<Integer> fragmentsAlreadyMerged = new HashSet<>();
        final List<String> listOfMergedPairsAndOrUnmergedFragments = new ArrayList<>();
        
        while (fragmentsAlreadyMerged.size() < toPairwiseMerge.size()) {
            int maxOverlap = 0;
            Entry<IndicesOfPair, OverlapDetails> greedyChoice = null;
            for (Entry<IndicesOfPair, OverlapDetails> entry : pairIndicesAndOverlapDetails.entrySet()) {
                
                int indexOfFirstItemInPair = entry.getKey().firstIndex();
                int indexOfSecondItemInPair = entry.getKey().secondIndex();

                if (!fragmentsAlreadyMerged.contains(indexOfFirstItemInPair) && !fragmentsAlreadyMerged.contains(indexOfSecondItemInPair)) {
                    if (entry.getValue().overlapAmount() > maxOverlap) {
                        maxOverlap = entry.getValue().overlapAmount();
                        greedyChoice = entry;
                    }
                }
                
            }

            if (greedyChoice == null) break; // There are no further merges that we can do at this stage
            listOfMergedPairsAndOrUnmergedFragments.add(greedyChoice.getValue().mergedString());
            
            fragmentsAlreadyMerged.add(greedyChoice.getKey().firstIndex());
            fragmentsAlreadyMerged.add(greedyChoice.getKey().secondIndex());
        }

        // Add back unmerged fragments

        for (int i = 0; i < toPairwiseMerge.size(); i++) {
            if (!fragmentsAlreadyMerged.contains(i)) {
                listOfMergedPairsAndOrUnmergedFragments.add(toPairwiseMerge.get(i));
            }
        }

        return listOfMergedPairsAndOrUnmergedFragments;
    }

    private HashMap<IndicesOfPair, OverlapDetails> generatePairsOfFragmentsInBothOrdersAndMergeData(final List<String> toPairwiseMerge) {
        final HashMap<IndicesOfPair, OverlapDetails> pairIndicesAndOverlapDetails = new HashMap<>();

        // Finds all pairwise merges, recording the indices of the chunks merge as a pair, the size of the overlap, and the corresponding merged string (of the relevant pair)
        for (int i = 0; i < toPairwiseMerge.size(); i++) {
            for (int j = 0; j < toPairwiseMerge.size(); j++) {
                if (i == j) continue; 
                OverlapDetails potentialMerge = findSuffixPrefixOverlap(toPairwiseMerge.get(i), toPairwiseMerge.get(j));
                pairIndicesAndOverlapDetails.put(new IndicesOfPair(i, j), potentialMerge);
            }
        }
        return pairIndicesAndOverlapDetails;
    }

    /* 
        Starts with the largest suffix, and progressively reduces its size
        Checks the suffix is the same as the second strings prefix i.e. overlap checking
        Returns the first match, which is the largest due to the iteration order
    */
    private OverlapDetails findSuffixPrefixOverlap(String first, String second) {
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
