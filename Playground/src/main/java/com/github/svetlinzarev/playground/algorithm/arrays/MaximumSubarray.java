package com.github.svetlinzarev.playground.algorithm.arrays;

import java.util.Arrays;
import java.util.Objects;

public class MaximumSubarray {

    public static class Subarray {
        final int[] array;
        final int start;
        final int end;
        final long value;

        public Subarray(int[] array, int start, int end, long value) {
            this.array = Objects.requireNonNull(array);
            this.start = start;
            this.end = end;
            this.value = value;
        }

        public int[] getArray() {
            return array;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(array);
            result = prime * result + end;
            result = prime * result + start;
            result = prime * result + (int) (value ^ (value >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Subarray other = (Subarray) obj;
            if (!Arrays.equals(array, other.array)) {
                return false;
            }
            if (end != other.end) {
                return false;
            }
            if (start != other.start) {
                return false;
            }
            if (value != other.value) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Subarray [start=" + start + ", end=" + end + ", value=" + value + "]";
        }
    }

    private MaximumSubarray() {
        throw new AssertionError("This class is not meant to be instantiated");
    }

    public static Subarray max(int[] array) {
        int maxValue = array[0];
        int maxValueTmp = array[0];

        int start = 0;
        int startTmp = 0;
        int end = 0;

        for (int i = 1; i < array.length; i++) {
            if (maxValueTmp < 0) {
                maxValueTmp = array[i];
                startTmp = i;
            } else {
                maxValueTmp += array[i];
            }

            if (maxValueTmp >= maxValue) {
                maxValue = maxValueTmp;
                start = startTmp;
                end = i;
            }
        }

        return new Subarray(array, start, end, maxValue);
    }

}
