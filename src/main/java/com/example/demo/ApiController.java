/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author E2E
 */
@RestController
@RequestMapping("/nasa/temperature")
public class ApiController {

    public String urlString = "https://api.nasa.gov/insight_weather/?api_key=CQ0rZ2VaL3rmVAjGoVwYUzVKAT1RYxERjJNnz2cT&feedtype=json&ver=1.0";

    @RequestMapping(value = {"", "/"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map avgTemperature() {
        double av = 0;
        JSONObject jo = httpRequest();
        List<String> solList = GetAllSol(jo);
        for (String sol : solList) {
            JSONObject at = ((JSONObject) ((JSONObject) jo.get(sol)).get("AT"));
            av += (double) at.get("av");
        }
        av = av / solList.size();
        av = toFahrenheit(av);
        return Collections.singletonMap("averageTemperature", av);
    }

    @RequestMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map solTemperature(@PathVariable int id) {
        JSONObject jo = httpRequest();
        List<String> solList = GetAllSol(jo);
        double avg;
        String sol = String.valueOf(id);
        if (solList.contains(sol)) {
            avg = (double) ((JSONObject) ((JSONObject) jo.get(sol)).get("AT")).get("av");
            avg = toFahrenheit(avg);
            return Collections.singletonMap("averageTemperature", avg);
        }
        return null;
    }

    private JSONObject httpRequest() {
        try {
            URL url = new URL(urlString);
            InputStream is = url.openStream();
            byte[] readAllBytes = is.readAllBytes();
            Object obj = new JSONParser().parse(new String(readAllBytes));
            JSONObject jo = (JSONObject) obj;
            return jo;
        } catch (IOException | ParseException ex) {
            return null;
        }
    }

    private List<String> GetAllSol(JSONObject jo) {
        List<String> solList = new ArrayList<>();
        if (jo != null) {
            JSONArray arr = (JSONArray) jo.get("sol_keys");
            arr.forEach(item -> {
                solList.add(item.toString());
            });
        }
        return solList;
    }

    private double toFahrenheit(double t1) { 
        String s = String.format(Locale.US, "%.1f", t1 * (9f / 5f) + 32f);
        return Double.parseDouble(s);
    }
}
