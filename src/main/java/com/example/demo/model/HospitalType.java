package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum HospitalType {
    @JsonProperty("GOVERNMENT") GOVERNMENT,
    @JsonProperty("PRIVATE") PRIVATE
}
