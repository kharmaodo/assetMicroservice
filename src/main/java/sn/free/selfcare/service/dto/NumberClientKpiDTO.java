package sn.free.selfcare.service.dto;
//TODO : MD : Audit : Exploiter Lombok pour dit clean code
public class NumberClientKpiDTO implements Comparable<NumberClientKpiDTO> {
    private int monthNumber;
    private String monthName;
    private long numberOfClients;

    public NumberClientKpiDTO() {
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

    public long getNumberOfClients() {
        return numberOfClients;
    }

    public void setNumberOfClients(long numberOfClients) {
        this.numberOfClients = numberOfClients;
    }

    @Override
    public int compareTo(NumberClientKpiDTO o) {
        return this.monthNumber - o.getMonthNumber();
    }

    @Override
    public String toString() {
        return "NumberClientKpiDTO{" +
            "monthNumber=" + monthNumber +
            ", monthName='" + monthName + '\'' +
            ", numberOfClients=" + numberOfClients +
            '}';
    }
}
