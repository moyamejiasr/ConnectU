package com.onelio.connectu.Services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.AcademicYearRequest;
import com.onelio.connectu.API.HorarioRequest;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Helpers.UpdaterHelper;
import com.onelio.connectu.Managers.ErrorManager;

import java.util.Calendar;

public class UAUpdate extends IntentService {

    public enum YearDataLoc {
        YEARS,
        SUBJECTS,
        TEACHERS,
        C_DOSENCIA,
        C_EVALUACION,
        C_EXAMENES,
        C_FESTIVOS,
        COMPLETED
    }

    App app;

    private int result = Activity.RESULT_CANCELED;
    public static final String NOTIFICATION = "com.onelio.connectu.Services";
    private boolean isSilence = false;

    AcademicYearRequest request;
    HorarioRequest hrequest;

    public UAUpdate() {
        super("DownloadService");
    }

    // Start async task
    @Override
    protected void onHandleIntent(Intent intent) {
        app = (App) getApplication();
        Bundle bundle = intent.getExtras();
        isSilence = bundle.getString(Common.INTENT_KEY_UPDATE_TYPE).equals(Common.UAUPDATE_TYPE_SILENCE);
        //Start horario request
        hrequest = new HorarioRequest(getBaseContext());
        //Start year request
        request = new AcademicYearRequest(getBaseContext());
        request.loadYearsList(callback);
    }

    YearDataLoc loc = YearDataLoc.YEARS;
    String subject;
    int sloc = 0;
    String year;
    int yloc = 0;

    AcademicYearRequest.UserDataCallback callback = new AcademicYearRequest.UserDataCallback() {
        @Override
        public void onCompleted(boolean onResult, String message) {
            if (onResult) {
                switch(loc) {
                    case YEARS:
                        //Load year subjects
                        loc = YearDataLoc.SUBJECTS;
                        year = request.academicYears.get(yloc).getYear();
                        request.loadSubjectsByYear(year, callback);
                        break;
                    case SUBJECTS:
                        loc = YearDataLoc.TEACHERS;

                        if (request.academicYears.get(yloc).getSubjectsData().size() != 0) { //Has subjects?
                            //Continue searching
                            subject = request.academicYears.get(yloc).getSubjectsData().get(sloc).getId();
                            request.loadTeachersByYearAndSubject(year, subject, false, callback); //Load teachers
                        } else {
                            int ysize = request.academicYears.size() - 1;
                            if (yloc < ysize) { //Has more years??
                                //Go next year
                                sloc = 0;
                                yloc++;
                                result = Activity.RESULT_OK;
                                loc = YearDataLoc.YEARS;
                                publishResults(result, loc, message);
                                callback.onCompleted(true, "");
                            } else { //In case of last subject of prev year has no teacher
                                //End the process
                                request.saveAcademicYear(); //Continue next step
                                updateHorario();
                            }
                        }
                        break;
                    case TEACHERS:
                        int ysize = request.academicYears.size() - 1;
                        int ssize = request.academicYears.get(yloc).getSubjectsData().size() - 1;

                        if (sloc < ssize) { //Has more subjects??
                            sloc++;
                            loc = YearDataLoc.SUBJECTS;
                            callback.onCompleted(true, ""); //Load them
                        } else if (yloc < ysize) { //Has more years??
                            //Update status
                            result = Activity.RESULT_OK;
                            loc = YearDataLoc.YEARS;
                            publishResults(result, loc, message);
                            //Continue
                            sloc = 0;
                            yloc++;
                            callback.onCompleted(true, ""); //Load them
                        } else {
                            request.saveAcademicYear(); //Continue next step
                            updateHorario();
                        }
                        break;
                }
            } else {
                if (!message.equals(ErrorManager.FAILED_CONNECTION)) {
                    FirebaseCrash.log("Failed loading object number: " + loc.name());
                    FirebaseCrash.report(new Exception(message));
                }
                result = Activity.RESULT_CANCELED;
                publishResults(result, loc, message);
            }
        }
    };

    String htype = HorarioRequest.CALENDAR_DOCENCIA;
    long hstart;
    long hend;

    void updateHorario() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMinimum(Calendar.DAY_OF_MONTH));
        hstart = calendar.getTimeInMillis() / 1000;
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
        hend = calendar.getTimeInMillis() / 1000;
        //Update state
        result = Activity.RESULT_OK;
        loc = YearDataLoc.C_DOSENCIA;
        publishResults(result, loc, "");
        hrequest.loadHorario(hstart, hend, htype, hcallback);
    }

    HorarioRequest.HorarioCallback hcallback = new HorarioRequest.HorarioCallback() {
        @Override
        public void onCompleted(boolean onResult, String message) {
            if (onResult) {
                switch(loc) {
                    case C_DOSENCIA:
                        htype = HorarioRequest.CALENDAR_EVALUACION;
                        loc = YearDataLoc.C_EVALUACION;
                        result = Activity.RESULT_OK;
                        publishResults(result, loc, message);
                        hrequest.loadHorario(hstart, hend, htype, hcallback);
                        break;
                    case C_EVALUACION:
                        htype = HorarioRequest.CALENDAR_EXAMENES;
                        loc = YearDataLoc.C_EXAMENES;
                        result = Activity.RESULT_OK;
                        publishResults(result, loc, message);
                        hrequest.loadHorario(hstart, hend, htype, hcallback);
                        break;
                    case C_EXAMENES:
                        htype = HorarioRequest.CAlENDAR_FESTIVOS;
                        loc = YearDataLoc.C_FESTIVOS;
                        result = Activity.RESULT_OK;
                        publishResults(result, loc, message);
                        hrequest.loadHorario(hstart, hend, htype, hcallback);
                        break;
                    case C_FESTIVOS:
                        loc = YearDataLoc.COMPLETED;
                        result = Activity.RESULT_FIRST_USER;
                        hrequest.saveFullHorario();
                        publishResults(result, loc, message);
                        //Finish Update & Save update time
                        app.lastUpdateTime = UpdaterHelper.changeUpdatedDate(getBaseContext());
                        break;
                }
            } else {
                FirebaseCrash.log("Failed loading object number: " + loc.name());
                FirebaseCrash.report(new Exception(message));
                result = Activity.RESULT_CANCELED;
                publishResults(result, loc, message);
            }
        }
    };


    private void publishResults(int result, YearDataLoc loc, String error) {
        if (!isSilence) {
            Intent intent = new Intent(NOTIFICATION);
            intent.putExtra(Common.INTENT_KEY_RESULT, result);
            intent.putExtra(Common.INTENT_KEY_LOC, loc);
            intent.putExtra(Common.INTENT_KEY_ERROR, error);
            sendBroadcast(intent);
        }
    }
}