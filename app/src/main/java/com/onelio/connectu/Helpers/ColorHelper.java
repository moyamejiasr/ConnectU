package com.onelio.connectu.Helpers;

import com.onelio.connectu.R;

public class ColorHelper
{
    public static String getColor(char c) {
        String color = "#424242";
        switch (c) {
            case 'A':
                color = "#E91E63";
                break;
            case 'B':
                color = "#F44336";
                break;
            case 'C':
                color = "#9C27B0";
                break;
            case 'D':
                color = "#3F51B5";
                break;
            case 'E':
                color = "#2196F3";
                break;
            case 'F':
                color = "#009688";
                break;
            case 'G':
                color = "#FFEB3B";
                break;
            case 'H':
                color = "#FF5722"; //R
                break;
            case 'I':
                color = "#E91E63";
                break;
            case 'J':
                color = "#F44336";
                break;
            case 'K':
                color = "#9C27B0";
                break;
            case 'L':
                color = "#3F51B5";
                break;
            case 'M':
                color = "#2196F3";
                break;
            case 'N':
                color = "#009688";
                break;
            case 'O':
                color = "#FF4081";
                break;
            case 'P':
                color = "#FF5722"; //R
                break;
            case 'Q':
                color = "#E91E63";
                break;
            case 'R':
                color = "#F44336";
                break;
            case 'S':
                color = "#9C27B0";
                break;
            case 'T':
                color = "#3F51B5";
                break;
            case 'U':
                color = "#2196F3";
                break;
            case 'V':
                color = "#009688";
                break;
            case 'Z':
                color = "#FFEB3B";
                break;
            case 'Y':
                color = "#FF5722"; //R
                break;
        }
        return color;
    }

    public static int appGetSrc(int position) {
        int color = 0;
        switch (position) {
            case 0:
                color = R.drawable.ic_anuncios;
                break;
            case 1:
                color = R.drawable.ic_materiales;
                break;
            case 2:
                color = R.drawable.ic_profesores;
                break;
            case 3:
                color = R.drawable.ic_tutorias;
                break;
            case 4:
                color = R.drawable.ic_moodle;
                break;
            case 5:
                color = R.drawable.ic_horario;
                break;
            case 6:
                color = R.drawable.ic_evaluacion;
                break;
            case 7:
                color = R.drawable.ic_expediente;
                break;
            case 8:
                color = R.drawable.ic_uaproject;
                break;
            case 9:
                color = R.drawable.ic_otroservicios;
                break;
            case 10:
                color = R.drawable.ic_mapa;
                break;
            case 11: //TODO BETA ONLY!, DELETE WHEN FINISHED
                color = R.drawable.ic_contacto;
                break;
        }
        return color;
    }

}
