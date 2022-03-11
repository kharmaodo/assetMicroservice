package sn.free.selfcare.service.dto;
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
public class NumberClientActiveKpiDTO implements Comparable<NumberClientActiveKpiDTO> {
    private int monthNumber;
    private String monthName;
    private long numberOfActiveClients;

    public NumberClientActiveKpiDTO() {
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

    public long getNumberOfActiveClients() {
        return numberOfActiveClients;
    }

    public void setNumberOfActiveClients(long numberOfActiveClients) {
        this.numberOfActiveClients = numberOfActiveClients;
    }

    @Override
    public int compareTo(NumberClientActiveKpiDTO o) {
        return this.monthNumber - o.getMonthNumber();
    }

    @Override
    public String toString() {
        return "NumberClientActifKpiDTO{" +
            "monthNumber=" + monthNumber +
            ", monthName='" + monthName + '\'' +
            ", numberOfActiveClients=" + numberOfActiveClients +
            '}';
    }
}
