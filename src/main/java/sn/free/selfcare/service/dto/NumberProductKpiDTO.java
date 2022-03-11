package sn.free.selfcare.service.dto;
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
public class NumberProductKpiDTO implements Comparable<NumberProductKpiDTO> {
    private int monthNumber;
    private String monthName;
    private long numberOfProducts;

    public NumberProductKpiDTO() {
    }

    public int getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public long getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setNumberOfProducts(long numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }

    @Override
    public int compareTo(NumberProductKpiDTO o) {
        return this.monthNumber - o.getMonthNumber();
    }

    @Override
    public String toString() {
        return "NumberProductKpiDTO{" +
            "monthNumber=" + monthNumber +
            ", monthName='" + monthName + '\'' +
            ", numberOfProducts=" + numberOfProducts +
            '}';
    }
}
