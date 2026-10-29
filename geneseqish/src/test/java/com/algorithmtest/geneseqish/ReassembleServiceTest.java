package com.algorithmtest.geneseqish;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ReassembleServiceTest {

    private final ReassembleService reassembleService = new ReassembleService();

    @ParameterizedTest(name = "Input \"{0}\" should result in \"{1}\"")
    @MethodSource("provideExamples")
    public void testExamples(String input, String expected) {

        final String result = reassembleService.reassemble(input);

        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideExamples() {
        return Stream.of(
            Arguments.of(
                            "This ;s is an exam;xample probl;blem.", 
                            "This is an example problem."),
            Arguments.of(
                            "nother.;his is an;Th", 
                            "This is another."),
            Arguments.of(
                            "A m;mangled ph;phrase.;A man;angled ;ed phrase. ", 
                            "A mangled phrase. "),
            Arguments.of(
                            "ogramming;Java pr;progr", 
                            "Java programming"),
            Arguments.of(
                            "Hello Wo;o World", 
                            "Hello World"),
            Arguments.of(
                            "The big cat sat;cat;cat sat on the mat", 
                            "The big cat sat on the mat"),
            Arguments.of(
                            "Duplicate;Duplicate;Duplicate text", 
                            "Duplicate text"),
            Arguments.of(
                            "Doesn't ;merge.", 
                            "Doesn't ;merge."),
            Arguments.of(
                            "Hello Wo;o World; Surprise", 
                            "Hello World; Surprise"),
            Arguments.of(
                            "BC;CDE;ABC", 
                            "ABCDE"),
            Arguments.of(
                            "A;B;C", 
                            "A;B;C"),
            Arguments.of(
                            "document fragment;fragment;fragment reassembly", 
                            "document fragment reassembly"),
            Arguments.of(
                            "Hello Wo;o World; Surprise; Chonk", 
                            "Hello World; Surprise; Chonk"),
            Arguments.of(
                            "document fragment;fragment;fragment reassembly;gop;chonk", 
                            "document fragment reassembly;gop;chonk"),
            Arguments.of(
                            "", 
                            ""),
            Arguments.of(
                            "SingleFragment", 
                            "SingleFragment"),
            Arguments.of(
                            "ABC;DEF;GHI", 
                            "ABC;DEF;GHI"),
            Arguments.of(
                            "XYZ;XYZ;XYZ", 
                            "XYZ"),
            Arguments.of(
                            "An apple a day;apple a day keeps;day keeps the doctor", 
                            "An apple a day keeps the doctor"),
            Arguments.of(
                            "overlapping;g;g g", 
                            "overlapping g"),
            Arguments.of(
                            "cde;abcdef", 
                            "abcdef"),
            Arguments.of(
                            "ney;Geney;Fragments;Geney Fragments", 
                            "Geney Fragments"),
            Arguments.of(
                            "BCD;ABC;XAB", 
                            "XABCD"),
            Arguments.of(
                            "ABC;BCD;CDA;DAB", 
                            "ABCDAB"),
            Arguments.of(
                            "Hello ; World", 
                            "Hello World"),
            Arguments.of(
                            "abcde;efgh;cdef", 
                            "abcdefgh"),
            Arguments.of(
                            "mississippi;ssippi;missis", 
                            "mississippi"),
            Arguments.of(
                            "st;testing ;ing fragments;g fr;test;g f", 
                            "testing fragments"),
            Arguments.of(
                            "echo;chocolate;fast;gofaster", 
                            "gofaster;echocolate"),
            Arguments.of(
                            "abc;bcd;cdef;fgh;hij", 
                            "abcdefghij"),
            Arguments.of(
                            "A;B;A;B;AB", 
                            "AB")
        );
    }
}
