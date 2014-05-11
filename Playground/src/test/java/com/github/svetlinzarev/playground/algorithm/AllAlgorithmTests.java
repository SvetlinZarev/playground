package com.github.svetlinzarev.playground.algorithm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.svetlinzarev.playground.algorithm.arrays.AllArrayAlgorithmTests;
import com.github.svetlinzarev.playground.algorithm.rmq.AllRMQTests;
import com.github.svetlinzarev.playground.algorithm.sort.AllSortingAlgorithmsTests;

@RunWith(Suite.class)
@SuiteClasses({ AllSortingAlgorithmsTests.class, AllRMQTests.class, AllArrayAlgorithmTests.class })
public class AllAlgorithmTests {

}
