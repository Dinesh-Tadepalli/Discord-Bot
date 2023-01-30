package com.example;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.channel.GenericChannelEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.user.GenericUserEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.lang.String;
import javax.security.auth.login.LoginException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends ListenerAdapter {
    public static void main(String[] args) throws LoginException {

        JDA bot = JDABuilder.createDefault("TOKEN_ABCXYZ") // This is supposed to be where I put my Discord bot token. Confidential!
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new Main())
                .build();

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Chicago"));
        ZonedDateTime nextRun = now.withHour(3).withMinute(00).withSecond(00);
        if (now.compareTo(nextRun) > 0)
            nextRun = nextRun.plusDays(1);

        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {

            public void run() {
                for (int i = 0; i < bot.getTextChannels().size(); i++) {
                    String events = schedule(1);
                    try {
                        bot.getTextChannels().get(i).sendMessage(events).queue();
                    } catch (Exception E) {

                    }
                }
                ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
                Runnable task2 = () -> System.out.println("Running task2...");
                ses.schedule(task2, 5, TimeUnit.SECONDS);
            }

        }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        try {
            if (event.getMessage().getContentRaw().equals("!w")) {
                String events = schedule(0);
                event.getChannel().sendMessage(events).queue();
            } else if (event.getMessage().getContentRaw().equals("!wnext")) {
                String events = schedule(1);
                event.getChannel().sendMessage(events).queue();
            } else if (event.getMessage().getContentRaw().equals("!wnext " + event.getMessage().getContentRaw().charAt(7))) {
                int next = event.getMessage().getContentRaw().charAt(7);
                next = next - 48;
                String events = schedule(next);
                event.getChannel().sendMessage(events).queue();
            } else if (event.getMessage().getContentRaw().equals("!wnext " + event.getMessage().getContentRaw().substring(event.getMessage().getContentRaw().length() - 2))) {
                String num = event.getMessage().getContentRaw().substring(event.getMessage().getContentRaw().length() - 2);
                int next = Integer.parseInt(num);
                String events = schedule(next);
                event.getChannel().sendMessage(events).queue();
            } else if (event.getMessage().getContentRaw().equals("!wnext " + event.getMessage().getContentRaw().substring(event.getMessage().getContentRaw().length() - 3))) {
                String num = event.getMessage().getContentRaw().substring(event.getMessage().getContentRaw().length() - 3);
                int next = Integer.parseInt(num);
                String events = schedule(next);
                event.getChannel().sendMessage(events).queue();
            }
        } catch (Exception e) {

        }
    }

    public static String schedule(int next) {
        TimeZone time_zone = TimeZone.getTimeZone("CST");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(time_zone);
        calendar.add(calendar.DATE, next);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        CreateList list = new CreateList();
        list.add("bloodtree");
        list.add("2");
        list.add("outcrop");
        list.add("2");
        list.add("butterfly");
        list.add("1");
        list.add("kbd");
        list.add("4");
        list.add("infernal star");
        list.add("3");
        list.add("define");
        list.add("1");

        String firstEvent = "";

        int hourOfYear = (24*dayOfYear);
        int hourOfDay = 0;
        int x = hourOfYear % 13;

        boolean go = true;
        while (go) {
            if (x == 0) {
                firstEvent = "outcrop";
                go = false;
            } else if (x == 2) {
                firstEvent = "butterfly";
                go = false;
            } else if (x == 3) {
                firstEvent = "kbd";
                go = false;
            } else if (x == 7) {
                firstEvent = "infernal star";
                go = false;
            } else if (x == 10) {
                firstEvent = "define";
                go = false;
            } else if (x == 11) {
                firstEvent = "bloodtree";
                go = false;
            } else {
                x++;
                hourOfDay++;
            }
        }

        ArrayList<String> newList = new ArrayList<String>();
        ArrayList<String> hourList = new ArrayList<String>();
        ArrayList<String> newHourList = new ArrayList<String>();

        Node current = list.head;
        do {
            if (firstEvent == current.data) {
                newList.add(firstEvent);
                hourList.add(Integer.toString(hourOfDay));
                current = current.next;
                hourOfDay = hourOfDay + Integer.valueOf(current.data.toString());
                current = current.next;
                break;

            } else {
                current = current.next.next;
            }
        } while(hourOfDay <= 23);

        while(hourOfDay <= 24) {
            newList.add(current.data.toString());
            hourList.add(Integer.toString(hourOfDay));
            current = current.next;
            hourOfDay = hourOfDay + Integer.valueOf(current.data.toString());
            current = current.next;
        }

        for (int i=0; i < hourList.size(); i++) {
            newHourList.add(hourList.get(i));
        }

        for (int i = 0; i < hourList.size(); i++) {
            if (Integer.valueOf(hourList.get(i)) >= 13) {
                newHourList.set(i, Integer.toString(Integer.valueOf(hourList.get(i)) - 12));
            }
        }

        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String schedule = "";
        schedule = schedule + month+"/"+day;
        schedule = schedule + "\n\n";
        for (int i = 0; i < hourList.size(); i++) {
            if (Integer.valueOf(hourList.get(i)) >= 6) {
                schedule = schedule + newHourList.get(i);
                if (Integer.valueOf(hourList.get(i)) == 0 || Integer.valueOf(hourList.get(i)) <= 11 ||
                        Integer.valueOf(hourList.get(i)) == 24) {
                    schedule = schedule + "am ";
                } else {
                    schedule = schedule + "pm ";
                }
                schedule = schedule + newList.get(i) + "\n";
            }
        }

        schedule = schedule + "\n";

        return schedule;
    }


}

class Node{
    String data;
    Node next;
    public Node(String data) {
        this.data = data;
    }
}
class CreateList<String> {
    //Represents the node of list.

    //Declaring head and tail pointer as null.
    public Node head = null;
    public Node tail = null;

    //This function will add the new node at the end of the list.
    public void add(String data){
        //Create new node
        Node newNode = new Node((java.lang.String) data);
        //Checks if the list is empty.
        if(head == null) {
            //If list is empty, both head and tail would point to new node.
            head = newNode;
            tail = newNode;
            newNode.next = head;
        }
        else {
            //tail will point to new node.
            tail.next = newNode;
            //New node will become new tail.
            tail = newNode;
            //Since, it is circular linked list tail will point to head.
            tail.next = head;
        }
    }

    //Displays all the nodes in the list
    public void display() {
        Node current = head;
        if(head == null) {
            System.out.println("List is empty");
        }
        else {
            System.out.println("Nodes of the circular linked list: ");
            do{
                //Prints each node by incrementing pointer.
                System.out.print(" "+ current.data);
                current = current.next;
            }while(current != head);
            System.out.println();
        }
    }

}
