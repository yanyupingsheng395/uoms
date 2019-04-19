package com.linksteady.operate.service;

import com.linksteady.operate.domain.ReasonRelMatrix;

import java.util.List;
import java.util.Map;

public interface ReasonMatrixService {

    Map<String, Object> getMatrix(String reasonId);
}
