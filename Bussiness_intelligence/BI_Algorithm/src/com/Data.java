package com;

public class Data {
	private static int DATA_NO = 10000;
	private static int PRODUCT_NO = 100;
	private double threshold = 0.50;
	private static double[] possibility1;
	private static double[] possibility2;

	public Data() {
		double[] possibility1 = new double[100];
		double[] possibility2 = new double[100];
		for (int i = 0; i < PRODUCT_NO; i++) {
			possibility1[i] = Math.random();
			possibility2[i] = Math.random();
		}
	}

	public int[][] getRandomData() {
		int[][] data = new int[1000][100];
		for (int i = 0; i < DATA_NO; i++) {
			int[] itemset = new int[100];

			double possibility_first = Math.random();
			double[] possibility_other = new double[100];
			for (int j = 0; j < PRODUCT_NO; j++) {
				possibility_other[j] = Math.random();
			}

			if (possibility_first > threshold) {
				for (int j = 0; j < PRODUCT_NO; j++) {
					itemset[j] = 0;
					if (possibility_other[j] < possibility1[j])
						itemset[j] = j;	
				}
			} else {
				for (int j = 0; j < PRODUCT_NO; j++) {
					if (possibility_other[j] < possibility2[j])
						itemset[j] = j;
				}
			}

			data[i] = itemset;
		}
		return data;
	}

}
