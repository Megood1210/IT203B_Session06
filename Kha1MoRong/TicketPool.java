package Kha1MoRong;

import java.util.ArrayList;
import java.util.List;

public class TicketPool {
    private String roomName;
    private List<Ticket> tickets;
    private int ticketCounter = 0;

    public TicketPool(String roomName, int total) {
        this.roomName = roomName;
        tickets = new ArrayList<>();

        for (int i = 1; i <= total; i++) {
            ticketCounter++;
            String id = roomName + "-" + String.format("%03d", ticketCounter);
            tickets.add(new Ticket(id, roomName));
        }
    }

    public synchronized Ticket sellTicket() {
        for (Ticket t : tickets) {
            if (!t.isSold()) {
                t.setSold(true);
                return t;
            }
        }
        return null;
    }

    public synchronized void addTickets(int count) {
        for (int i = 0; i < count; i++) {
            ticketCounter++;
            String id = roomName + "-" + String.format("%03d", ticketCounter);
            tickets.add(new Ticket(id, roomName));
        }
        System.out.println("Nhà cung cấp: Đã thêm " + count + " vé vào phòng " + roomName);
    }

    public int getRemainingTickets() {
        int count = 0;
        for (Ticket t : tickets) {
            if (!t.isSold()) {
                count++;
            }
        }
        return count;
    }
}