package com.example.assignment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelper {
    private final DatabaseReference databaseReference;

    public FirebaseHelper() {
        // Initialize the database reference to "test_logs" node
        databaseReference = FirebaseDatabase.getInstance().getReference("test_logs");
    }

    // Insert or update a test log record
    public void insertTestLog(TestLog testLog) {
        // using testNo as the unique key for this example
        databaseReference.child(String.valueOf(testLog.testNo)).setValue(testLog);
    }

    // Attach a listener to get updates from the "test_logs" node
    public void attachTestLogListener() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Loop through the children of "test_logs"
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TestLog testLog = snapshot.getValue(TestLog.class);
                    // Process the test log record here
                    System.out.println("TestLog: " + testLog.playerName);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.err.println("Failed to read value: " + error.toException());
            }
        });
    }
}
