package io.indoorlocation.navisens;

import com.navisens.motiondnaapi.MotionDna;

public interface LocationListener {
    void onLocationChange(MotionDna.Location location);
}
