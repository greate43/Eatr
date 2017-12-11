package sk.greate43.eatr.services;

import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class CheckOrderStatusService extends JobService {
    private static final String TAG = "CheckOrderStatusService";
    long added;
    long when;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;

    @Override
    public boolean onStartJob(JobParameters params) {
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    return;
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    // Food post = ds.getValue(Food.class);
                    if (ds.child("greate43").getValue() != null) {
                        collectSeller((Map<String, Object>) ds.child("greate43").getValue());
                    }
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        Log.d(TAG, "onStartJob: outside " + hasOrderExpired(added, when));
        if (hasOrderExpired(added, when)) {
            mDatabaseReference.child("eatr").child("greate43").child(params.getTag()).child("checkIfOrderIsActive").setValue(false);
            Log.d(TAG, "onStartJob: inside " + hasOrderExpired(added, when));

            return false;
        }


        Log.d(TAG, "onStartJob: ");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return false;
    }

    private boolean hasOrderExpired(long added, long when) {
        long now = System.currentTimeMillis();
        if (now > when) {
            return false;
        } else {
            long difference90 = (long) (0.9 * (when - added));
            return (now > (added + difference90)) ? true : false;
        }
    }

    private void collectSeller(Map<String, Object> value) {


        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            //Get seller map
            Map singleUser = (Map) entry.getValue();
            //Get seller field and append to list

//                   ,

            if (singleUser.get("expiryTime") != null) {
                when = (long) singleUser.get("expiryTime");
            }
            if (singleUser.get("timeStamp") != null) {
                added = Long.parseLong(singleUser.get("timeStamp").toString());
            }

        }

    }

}
