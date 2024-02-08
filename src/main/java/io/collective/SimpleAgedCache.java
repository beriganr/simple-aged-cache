package io.collective;

import java.time.Clock;
import java.time.Instant;

public class SimpleAgedCache {
    private Clock clock;

    private ExpirableEntry head;
    public SimpleAgedCache(Clock clock) {
        this.clock = clock;
    }

    public SimpleAgedCache() {
        this.clock = Clock.systemDefaultZone();
    }

    public void put(Object key, Object value, int retentionInMillis) {
        Instant expireTime = clock.instant().plusMillis(retentionInMillis);
        ExpirableEntry newEntry = new ExpirableEntry(key, value, expireTime);

        if (head != null) {
            newEntry.next = head;
        }
        head = newEntry;
    }

    public boolean isEmpty() {
        clearExpired();
        System.out.println(head == null);
        return head == null;

    }

    public int size() {

        clearExpired();
        int count = 0;
        ExpirableEntry curr = head;

        while(curr != null ){
            curr = curr.next;
            count++;
        }
        return count;
    }

    public Object get(Object key) {
        ExpirableEntry curr = head;

        while(curr != null){
            if (curr.key == key){
                return curr.value;
            }
            else{
                curr = curr.next;
            }
        }
        return null;
    }

    private void clearExpired(){
        ExpirableEntry curr = head;
        ExpirableEntry prev = null;

        while(curr != null){
            if(curr.isExpired(clock.instant())){
                if(prev == null){
                    head = curr.next;
                }
                else{
                    prev.next = curr.next;
                }
            }
            else{
                prev = curr;
            }
            curr = curr.next;
        }
    }
    private class ExpirableEntry {
        private Object key;
        private Object value;
        private Instant expireTime;
        private ExpirableEntry next;

        ExpirableEntry(Object key, Object value, Instant expireTime) {
            this.key = key;
            this.value = value;
            this.expireTime = expireTime;
            this.next = null;
        }

        boolean isExpired(Instant currTime) {
            if(currTime.isAfter(this.expireTime)){
                return true;
            }
            else {
                return false;
            }
        }
    }
}

