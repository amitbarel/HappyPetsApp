package dev.happypets.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

import dev.happypets.Database.DataManager;
import dev.happypets.Fragments.HomeFragment;
import dev.happypets.Fragments.ProfileFragment;
import dev.happypets.Fragments.QuestionsAnswersFragment;
import dev.happypets.Objects.Answer;
import dev.happypets.Objects.Question;
import dev.happypets.R;

public class MainActivity extends AppCompatActivity {

    DataManager dataManager;

    HomeFragment homeFragment = new HomeFragment();
    QuestionsAnswersFragment qaFragment = new QuestionsAnswersFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataManager = DataManager.getInstance(getApplicationContext());

        readFromDatabase();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, homeFragment).commit();
                return true;
            } else if (itemId == R.id.qa) {
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, qaFragment).commit();
                return true;
            } else if (itemId == R.id.profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, profileFragment).commit();
                return true;
            }
            return false;
        });
    }

    private void readFromDatabase() {
        final ArrayList<Question> questions = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("questions");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    questions.add(dataManager.getQuestion(dataSnapshot));
                }
                dataManager.setQuestions(questions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,
                        "error reading from firebase ",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}