package com.github.svetlinzarev.playground.algorithm.sort;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.svetlinzarev.playground.algorithm.sort.bubblesort.AllBubbleSortTests;
import com.github.svetlinzarev.playground.algorithm.sort.insertionsort.AllInsertionSortTests;
import com.github.svetlinzarev.playground.algorithm.sort.mergesort.AllMergeSortTests;
import com.github.svetlinzarev.playground.algorithm.sort.quizcksort.QuickSortTest;
import com.github.svetlinzarev.playground.algorithm.sort.selectionsort.SelectionSortTest;

@RunWith(Suite.class)
@SuiteClasses({ AllMergeSortTests.class, AllInsertionSortTests.class, AllBubbleSortTests.class, SelectionSortTest.class,
    QuickSortTest.class })
public class AllSortingAlgorithmsTests {

}
