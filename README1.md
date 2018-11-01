The two method are contained in the Solution.class

For both algorithms (funtions) the return value is the same, it's a double array of size 3. And double[0] denotes the max profit;
double[1] denotes the start day; double[2] denotes the end day.

For the input of DP(), it's just a double[] constructed from the input called rates. DP(rates)
For the input of DandC(), apart from rates(double[]), it also need the start of rates which is 0, and end of rates which is rates.length-1. DandC(rates,0,rates.length-1)

Hence, in order to run the code, create a main.class, and write a for loop for how many problems need to solve, and then for each
line in the input file, create a double array---rates, then just use it as input of DP() and DandC() as I illustrated above.

For the output file, you have to write it in accordance with the output of the funtion as I illustrated above.
