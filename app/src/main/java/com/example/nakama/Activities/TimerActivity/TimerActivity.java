package com.example.nakama.Activities.TimerActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nakama.Activities.AttemptSummaryActivity.RingSummaryActivity;
import com.example.nakama.Activities.OverallImpressionActivity.OverallImpressionActivity;
import com.example.nakama.DataBase.AppDatabase;
import com.example.nakama.DataBase.Entities.Users.Users;
import com.example.nakama.R;
import com.example.nakama.Services.TimerService;
import com.example.nakama.SharedPreferences.AppPreferences;
import com.example.nakama.Utils.Converter;
import com.example.nakama.Utils.Dictionary;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class TimerActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_TIME = "TIME";
    public static final String INTENT_EXTRA_SCORE = "SCORE";
    public static final String INTENT_FORCED_FINISH = "FORCED_FINISH";
    TimerActivityViewManager viewManager;
    private Intent timerServiceIntent;
    PowerManager.WakeLock wakeLock;
    PowerManager powerManager;
    AppPreferences appPreferences;
    AppDatabase db;
    Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appPreferences = new AppPreferences(getSharedPreferences(AppPreferences.NAME, Context.MODE_PRIVATE));
        switch (appPreferences.getDifficulty()){
            case Dictionary.Difficulty.Basic.NAME:
                setTheme(R.style.Theme_NAKAMA_BasicLevelTheme);
                break;
            case Dictionary.Difficulty.Advanced.NAME:
                setTheme(R.style.Theme_NAKAMA_AdvancedLevelTheme);
                break;
        }

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_timer);
        viewManager = new TimerActivityViewManager(TimerActivity.this);

        //Get current user from db
        db = AppDatabase.getInstance(this);
        user = db.getUser(appPreferences.getUserId());

        //Registering Broadcast Receiver for BrewingServiceMessages
        timerServiceIntent = new Intent(this, TimerService.class);
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.TIMER_ACTION)); //<----Register

        //Setting up power management
        powerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:mywakelog");
        wakeLock.acquire(15*60*1000L /*15 minutes*/);
        //Initialize default view
        viewManager.setViewToDefaultState(
                appPreferences.getDifficulty(),
                appPreferences.getActiveRing(),
                user);
        viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
        viewManager.setFunctionalButtonsEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.TIMER_ACTION)); //<----Register
    }
    @Override
    protected void onStop() {
        unregisterReceiver(broadcastReceiver); //<-- Unregister broadcast receiver to avoid memory leak
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        stopService(timerServiceIntent);
        wakeLock.release();
        super.onDestroy();
    }

    //----------------------------------------------------BROADCAST RECEIVER--------------------------------------------------------------------

    /**
     * On receive broadcast from brewingService
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //get BROADCAST_TIMER_VALUE from broadcast and process it
            long timeLeft = intent.getLongExtra(TimerService.BROADCAST_TIMER_VALUE, Dictionary.getAttemptTime(appPreferences.getDifficulty()));
            if(!TimerService.forcedFinish){
                viewManager.setTimerCurrentTime(timeLeft);
            }
            if(timeLeft == 0){
                viewManager.setViewToFinishedState();
                showTimeUpDialog();
            }
        }
    };

    //----------------------------------------------------BUTTONS ACTIONS--------------------------------------------------------------------
    public void onPlayButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "Play button pressed");
        if(!TimerService.isServiceRunning){
            Log.v("ACTIVITY DEBUG:", "Attempting to start service");
            //Get time from textField
            timerServiceIntent.putExtra(TimerService.TIME, Converter.stringToMillis((String) viewManager.getTimerTextView().getText()));
            timerServiceIntent.putExtra(TimerService.POLLING_FREQUENCY, appPreferences.getPollingFrequency());
            this.startService(timerServiceIntent);
        }
        viewManager.setViewToPlayedState();
    }

    public void onPauseButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "Pause button pressed");
        TimerService.pauseTimer();
        stopTimerAndStopService();
        viewManager.setViewToPausedState();
    }

    public void onResetButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "Reset button pressed");
        showConfirmResetDialog();
    }

    public void onDoneButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "Done button pressed");
        if(db.userScoresDao().getUserScoresDisqualificationReason(appPreferences.getUserId(), appPreferences.getDifficulty(), appPreferences.getActiveRing()) == null){
            showConfirmAttemptDialog();
        }
    }

    public void onFalseAlarmButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "False alarm button pressed");
        showConfirmFalseAlarmDialog();
    }

    public void onPositiveAlarmButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "Positive alarm button pressed");
        showConfirmPositiveAlarmDialog();
    }

    public void onTreatDroppedButton(View view) {
        Log.v("ACTIVITY DEBUG:", "Treat dropped button pressed");
        showConfirmTreatDropDialog();
    }

    public void onDefecationButtonClick(View view) {
        Log.v("ACTIVITY DEBUG:", "Defecation button pressed");
        showConfirmDefecationDialog();
    }
    public void onDisqualifyButtonCLick(View view) {
        Log.v("ACTIVITY DEBUG:", "Disqualify button pressed");
        showConfirmDisqualificationDialog();
    }

    public void showTimeUpDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.timeout_dialog_title)
                .setMessage(R.string.timeout_dialog_message)
                .setPositiveButton(R.string.dialog_positive_ok_button, (dialogInterface, i) -> {
                    db.userScoresDao().updateScorePoints(appPreferences.getUserId(), appPreferences.getDifficulty(), appPreferences.getActiveRing(), 0);
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                })
                .show();
    }

    public void showConfirmResetDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(R.string.confirm_reset_dialog_message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> {
                    viewManager.setViewToDefaultState(
                            appPreferences.getDifficulty(),
                            appPreferences.getActiveRing(),
                            user);
                    db.clearUserScore(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    viewManager.setFunctionalButtonsEnabled(false);
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }

    public void showConfirmAttemptDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(R.string.confirm_attempt_dialog_message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> {
                    //getting duration of this run
                    int timeLeft = Converter.stringToMillis((String) viewManager.getTimerTextView().getText());
                    int runTimeInMillis = Dictionary.getAttemptTime(appPreferences.getDifficulty()) - timeLeft;
                    db.userScoresDao().updateUserScoresAttemptTime(appPreferences.getUserId(), appPreferences.getDifficulty(), appPreferences.getActiveRing(), Converter.millisToString(runTimeInMillis));
                    Intent intent = new Intent(this, OverallImpressionActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }

    public int calculateUserScore(){
        int baseScore = 200;
        int timeLeft = Converter.stringToMillis((String) viewManager.getTimerTextView().getText());
        if(timeLeft == 0){
            baseScore = 0;
        }
        return baseScore;
    }

    public void showConfirmFalseAlarmDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(R.string.confirm_false_alarm_dialog_message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> {
                    //add false alarm counter
                    int currentFalseAlarms = db.userScoresDao().getUserScoresFalseAlarms(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    currentFalseAlarms += 1;
                    //Update false alarms
                    db.userScoresDao().updateUserScoresFalseAlarms(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing(), currentFalseAlarms);
                    //Update score
                    int currentScore = db.userScoresDao().getUserScoresScore(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    if(currentFalseAlarms > Dictionary.getFalseAlarmsLimit(appPreferences.getDifficulty())){
                        stopTimerAndStopService();
                        viewManager.setViewToFinishedState();
                        showFalseAlarmLimitDialog();
                        return;
                    }
                    db.userScoresDao().updateScorePoints(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing(), currentScore - 50);
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }

    public void showConfirmPositiveAlarmDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(R.string.confirm_positive_alarm_dialog_message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> {
                    //add samples found
                    int samplesFound = db.userScoresDao().getUserScoresSamplesFound(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    samplesFound += 1;
                    //Update samples found
                    db.userScoresDao().updateUserScoresSamplesFound(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing(), samplesFound);
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    //If all samples found stop timer
                    if(samplesFound == Dictionary.getSamplesOnRing(appPreferences.getDifficulty())){
                        stopTimerAndStopService();
                        viewManager.setViewToFinishedState();
                        showAllSamplesFoundDialog();
                    }
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }

    public void showFalseAlarmLimitDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.false_alarms_reached_dialog_title)
                .setMessage(R.string.false_alarms_reached_dialog_message)
                .setPositiveButton(R.string.dialog_positive_ok_button, (dialogInterface, i) -> {
                    viewManager.setTimerCurrentTime(0);
                    db.userScoresDao().updateScorePoints(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing(), 0);
                    db.userScoresDao().updateUserScoresAttemptTime(
                            user.uid,
                            appPreferences.getDifficulty(),
                            appPreferences.getActiveRing(),
                            Converter.millisToString(Dictionary.getAttemptTime(appPreferences.getDifficulty())));
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    Intent intent = new Intent(this, RingSummaryActivity.class);
                    startActivity(intent);
                })
                .show();
    }

    public void  showAllSamplesFoundDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.all_samples_found_dialog_title)
                .setMessage(R.string.all_samples_found_dialog_message)
                .setPositiveButton(R.string.dialog_positive_ok_button, (dialogInterface, i) -> {
                })
                .show();
    }

    public void showConfirmTreatDropDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(R.string.confirm_treat_dropped_dialog_message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> {
                    int currentTreatDropped = db.userScoresDao().getUserScoresTreatDropped(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    currentTreatDropped += 1;
                    //Update false alarms
                    db.userScoresDao().updateUserScoresTreatDropped(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing(), currentTreatDropped);
                    //Update score
                    int currentScore = db.userScoresDao().getUserScoresScore(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    if(currentTreatDropped == 2){
                        stopTimerAndStopService();
                        viewManager.setViewToFinishedState();
                        showTreatDroppedLimitDialog();
                        return;
                    }
                    db.userScoresDao().updateScorePoints(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing(), currentScore - 30);
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }

    public void showTreatDroppedLimitDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.tread_dropped_limit_reached_dialog_title)
                .setMessage(R.string.tread_dropped_limit_reached_dialog_message)
                .setPositiveButton(R.string.dialog_positive_ok_button, (dialogInterface, i) -> {
                    viewManager.setTimerCurrentTime(0);
                    db.userScoresDao().updateScorePoints(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing(), 0);
                    db.userScoresDao().updateUserScoresAttemptTime(
                            user.uid,
                            appPreferences.getDifficulty(),
                            appPreferences.getActiveRing(),
                            Converter.millisToString(Dictionary.getAttemptTime(appPreferences.getDifficulty())));
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    Intent intent = new Intent(this, RingSummaryActivity.class);
                    startActivity(intent);
                })
                .show();
    }

    public void showConfirmDefecationDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(R.string.confirm_defecation_dialog_message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> {
                    boolean disqualified = false;
                    //Check if there is no other defecation on any other rings (on this difficulty) - if so its disqualification
                    if(db.checkAllRingsForAnyDefecation(user.uid,  appPreferences.getDifficulty())){
                       db.disqualifyContestant(user.uid, appPreferences.getDifficulty(), getResources().getString(R.string.defecation_disqualification_reason));
                       disqualified = true;
                    }
                    db.userScoresDao().updateUserScoresDefecation(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing(), true);
                    stopTimerAndStopService();
                    viewManager.setViewToFinishedState();
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    viewManager.setTimerCurrentTime(0);
                    db.userScoresDao().updateScorePoints(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing(), 0);
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    db.userScoresDao().updateUserScoresAttemptTime(
                            user.uid,
                            appPreferences.getDifficulty(),
                            appPreferences.getActiveRing(),
                            Converter.millisToString(Dictionary.getAttemptTime(appPreferences.getDifficulty())));
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    if(disqualified){
                        showDisqualificationDialog(getResources().getString(R.string.defecation_disqualification_reason));
                        return;
                    }
                    Intent intent = new Intent(this, RingSummaryActivity.class);
                    startActivity(intent);
                })

                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }

    public void showDisqualificationDialog(String reason) {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.disqualification_dialog_title)
                .setMessage(reason)
                .setPositiveButton(R.string.dialog_positive_ok_button, (dialogInterface, i) -> {
                    // TODO: 11.03.2023 Should lead to all rings summary 
                    Intent intent = new Intent(this, RingSummaryActivity.class);
                    startActivity(intent);
                })
                .show();
    }

    public void showConfirmDisqualificationDialog() {
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setTitle(R.string.confirm_dialog_title)
                .setMessage(R.string.confirm_disqualification_dialog_message)
                .setPositiveButton(R.string.dialog_positive_yes_button, (dialogInterface, i) -> {
                    stopTimerAndStopService();
                    viewManager.setViewToFinishedState();
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    viewManager.setTimerCurrentTime(0);
                    db.userScoresDao().updateScorePoints(user.uid, appPreferences.getDifficulty(), appPreferences.getActiveRing(), 0);
                    viewManager.updateScores(db, user, appPreferences.getDifficulty(), appPreferences.getActiveRing());
                    showDisqualificationReasonInputDialog();
                })
                .setNegativeButton(R.string.dialog_negative_button, (dialogInterface, i) -> {})
                .show();
    }

    public void showDisqualificationReasonInputDialog() {
        LayoutInflater inflater = TimerActivity.this.getLayoutInflater();
        View mView = inflater.inflate(R.layout.edit_text_view, null, false);
        EditText editText = mView.findViewById(R.id.textInput);
        new MaterialAlertDialogBuilder(TimerActivity.this)
                .setView(mView)
                .setTitle(R.string.disqualification_dialog_title)
                .setMessage(R.string.provide_disqualification_reason_dialog_message)
                .setPositiveButton(R.string.dialog_positive_ok_button, (dialogInterface, i) -> {
                    db.disqualifyContestant(user.uid, appPreferences.getDifficulty(), editText.getText().toString());
                    Intent intent = new Intent(this, RingSummaryActivity.class);
                    startActivity(intent);
                })
                .setCancelable(false)
                .show();
    }

    public void stopTimerAndStopService(){
        TimerService.cancelTimer();
        this.stopService(timerServiceIntent);
    }

    public String getText(EditText editText){
        return editText.getText().toString();
    }
}