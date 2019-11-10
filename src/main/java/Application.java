import com.google.common.collect.Collections2;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application {

    public static void main(String[] args) {

        Map<String, Map<String, Integer>> guests = initializeGuests();
        guests = addMe(guests);
        Collection<List<String>> permutations = Collections2.permutations(guests.keySet());
        int bestHappiness = 0;
        for(List guestList : permutations){
            Object[] guestArray = guestList.toArray();
            bestHappiness = calculateHappiness(guests, bestHappiness, guestArray);
        }
        System.out.println(bestHappiness);
    }

    private static int calculateHappiness(Map<String, Map<String, Integer>> guests, int bestHappiness, Object[] guestArray) {
        int totalHappiness = 0;
        int length = guestArray.length;
        for(int i = 0; i < length; i++) {
            Map guestMap = guests.get(guestArray[i]);
            int leftHappiness = (int) guestMap.get(guestArray[(i - 1 + length) % length]);
            int rightHappiness = (int) guestMap.get(guestArray[(i + 1 + length) % length]);
            totalHappiness += leftHappiness + rightHappiness;
        }
        if(bestHappiness < totalHappiness) {
            bestHappiness = totalHappiness;
        }
        return bestHappiness;
    }

    private static Map<String, Map<String, Integer>> initializeGuests() {
        InputStream in = Application.class.getResourceAsStream("guest.txt");
        Pattern pattern = Pattern.compile("(\\w+) would (\\w+) (\\d+) happiness units by sitting next to (\\w+).");
        Scanner sc = new Scanner(in);
        Map<String, Map<String, Integer>> guests = new HashMap<>();
        while(sc.hasNextLine()) {
            String s = sc.nextLine();
            Matcher m = pattern.matcher(s);
            if(m.find()){
                String guest = m.group(1);
                if(!guests.containsKey(guest)){
                    guests.put(guest, new HashMap<>());
                }
                int happiness = Integer.parseInt(m.group(3));
                if(m.group(2).equals("lose")){
                    happiness *= -1;
                }
                Map guestMap = guests.get(guest);
                guestMap.put(m.group(4), happiness);
                guests.put(guest, guestMap);
            }
        }
        return guests;
    }

    private static Map<String, Map<String, Integer>>  addMe(Map<String, Map<String, Integer>> guests) {
        Map<String, Integer> myMap = new HashMap<>();
        guests.entrySet().stream().forEach(guest -> {
            Map<String, Integer> guestMap = guest.getValue();
            guestMap.put("me", 0);
            guest.setValue(guestMap);
            myMap.put(guest.getKey(), 0);
        });
        guests.put("me",  myMap);
        return guests;
    }
}
