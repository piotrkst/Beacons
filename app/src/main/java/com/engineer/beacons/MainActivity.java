package com.engineer.beacons;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.Utils;
import com.estimote.sdk.cloud.model.BroadcastingPower;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final Map<String, List<String>> PLACES_BY_BEACONS;
    private BeaconManager beaconManager;
    private Region region;
    public String level = "1";
    public String lastState = "";
    public Integer counter = 0;
    public AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        beaconManager = new BeaconManager(this);

        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);

                    Log.i("Sila beacona", "Sila beacona: " + Utils.computeAccuracy(nearestBeacon));
                    Log.i("Sila proximity", "Sila proximity: " + Utils.computeProximity(nearestBeacon));

                    String places = placesNearBeacon(nearestBeacon);

                    if (lastState != Utils.computeProximity(nearestBeacon).toString() || counter == 0) {
                        lastState = Utils.computeProximity(nearestBeacon).toString();
                        Log.i("PLACES", "PLACES: " + places);
                        if (places != null) {
                            builder.setMessage(places)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                        }
                                    });
                            alert = builder.create();
                            alert.show();

                            counter = 10;
                        }

                    } else counter--;

                    if (counter == 0){
                        if (alert != null) alert.hide();
                    }
                    // TODO: update the UI here
                    Log.i("COUNTER", "COUNTER: " + counter);
                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put("20472:12236", new ArrayList<String>() {{
            add("Czujesz woń rozkładającego się w okolicy ciała ludzkiego");
            add("Wokół Ciebie krew płynie niczym rzeka, musisz to zbadać!");
            add("Znajdujesz ciało potencjalnego studenta jak wnioskujesz po rozrzuconych wokół niego notatkach. Wśród nich znajduje się podarte podanie do mgr Piłki o przeniesienie do innej grupy ćwiczeniowej.");
            add("1");
        }});
        placesByBeacons.put("41921:56029", new ArrayList<String>() {{
            add("Znajdujesz się w okolicy gabinetu mgr Piłki, który był adresatem podania. Może warto by tam zajrzeć?");
            add("Wchodzisz do pokoju mgr Piłki, burdel tutaj straszny. Postaraj się znaleźć jakieś wskazówki.");
            add("Znalazłeś kosz wypchany papierami, przeprowadzając dogłębną analizę tychże papierów okazuje się, że są to podania o przeniesienie do innej grupy ćwiczeniowej TEGO SAMEGO STUDENTA, który leżał martwy w holu głównym!");
            add("2");
            add("Znajdujesz się w okolicy gabinetu mgr Piłki.");
            add("Tuż przed Twoim nosem mgr Piłka wchodzi do swojego gabinetu.");
            add("Dopadasz mgr Piłke w jego gabinecie i przeprowadzasz bezpośrednie przesłuchanie, w wyniku którego mgr Piłka sam przyznaje się do popełniego przestępstwa. Gratulacje, udało Ci się rozwiązać zagadkę. Awansujesz na kolejny poziom.");
            add("4");
        }});
        placesByBeacons.put("45215:23952", new ArrayList<String>() {{
            add("Znajdujesz się na terenie laboratoriów. Tutaj studenci drukują podania. Spróbuj je przeszukać, może czegoś się dowiesz.");
            add("Tylko jeden komputer w tej sali jest włączony i nikogo przy nim nie ma, przypadek?");
            add("Badając stanowisko pracy, okazuje się, że zalogowanym osobnikiem jest student Jan Kowalski. Egzaminujesz jego otwarte okna. Jednym z nich jest otwarta korespondencja do mgr Piłki, w której to widnieją pogróżki względem życia studenta.");
            add("3");
        }});
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    private String placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        Log.i("LEVEL", "LEVEL : " + level);
        Log.i("BEACON KEY", "BEACON KEY: " + beaconKey);
        if (PLACES_BY_BEACONS.containsKey(beaconKey) && PLACES_BY_BEACONS.get(beaconKey).get(3) == level) {
            if(Utils.computeProximity(beacon).toString() == "FAR"){
                return PLACES_BY_BEACONS.get(beaconKey).get(0);
            }
            if(Utils.computeProximity(beacon).toString() == "NEAR"){
                return PLACES_BY_BEACONS.get(beaconKey).get(1);
            }
            if(Utils.computeProximity(beacon).toString() == "IMMEDIATE"){
                levelUp(PLACES_BY_BEACONS.get(beaconKey).get(2));
                return PLACES_BY_BEACONS.get(beaconKey).get(2);
            }
        } else {
            if (beacon.getMajor() == 41921 && beacon.getMinor() == 56029 && level == "4"){
                if(Utils.computeProximity(beacon).toString() == "FAR"){
                    return PLACES_BY_BEACONS.get(beaconKey).get(4);
                }
                if(Utils.computeProximity(beacon).toString() == "NEAR"){
                    return PLACES_BY_BEACONS.get(beaconKey).get(5);
                }
                if(Utils.computeProximity(beacon).toString() == "IMMEDIATE"){
                    levelUp(PLACES_BY_BEACONS.get(beaconKey).get(6));
                    return PLACES_BY_BEACONS.get(beaconKey).get(6);
                }
            }
        }
        return null;
    }

    public void levelUp(String text){
        Integer i = new Integer(level);
        if (alert != null) alert.hide();
        switch (i) {
            case 1:
                TextView labelLevelOne = (TextView) findViewById(R.id.levelOne);
                labelLevelOne.setText(text);
                break;
            case 2:
                TextView labelLevelTwo = (TextView) findViewById(R.id.levelTwo);
                labelLevelTwo.setText(text);
                break;
            case 3:
                TextView labelLevelThree = (TextView) findViewById(R.id.levelThree);
                labelLevelThree.setText(text);
                break;
            case 4:
                TextView labelLevelFour = (TextView) findViewById(R.id.levelFour);
                labelLevelFour.setText(text);
                break;
        }
        i = i + 1;
        level = i.toString();
    }
}
