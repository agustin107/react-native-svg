/*
 * Copyright (c) 2015-present, Horcrux.
 * All rights reserved.
 *
 * This source code is licensed under the MIT-style license found in the
 * LICENSE file in the root directory of this source tree.
 */


package com.horcrux.svg;

import android.graphics.Matrix;

import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.uimanager.annotations.ReactProp;

import javax.annotation.Nullable;

/**
 * Shadow node for virtual LinearGradient definition view
 */
class LinearGradientShadowNode extends DefinitionShadowNode {

    private String mX1;
    private String mY1;
    private String mX2;
    private String mY2;
    private ReadableArray mGradient;
    private Brush.BrushUnits mGradientUnits;

    private static final float[] sRawMatrix = new float[]{
        1, 0, 0,
        0, 1, 0,
        0, 0, 1
    };
    private Matrix mMatrix = null;


    @ReactProp(name = "x1")
    public void setX1(Dynamic x1) {
        if (x1.getType() == ReadableType.String) {
            mX1 = x1.asString();
        } else {
            mX1 = String.valueOf(x1.asDouble());
        }
        markUpdated();
    }

    @ReactProp(name = "y1")
    public void setY1(Dynamic y1) {
        if (y1.getType() == ReadableType.String) {
            mY1 = y1.asString();
        } else {
            mY1 = String.valueOf(y1.asDouble());
        }
        markUpdated();
    }

    @ReactProp(name = "x2")
    public void setX2(Dynamic x2) {
        if (x2.getType() == ReadableType.String) {
            mX2 = x2.asString();
        } else {
            mX2 = String.valueOf(x2.asDouble());
        }
        markUpdated();
    }

    @ReactProp(name = "y2")
    public void setY2(Dynamic y2) {
        if (y2.getType() == ReadableType.String) {
            mY2 = y2.asString();
        } else {
            mY2 = String.valueOf(y2.asDouble());
        }
        markUpdated();
    }

    @ReactProp(name = "gradient")
    public void setGradient(ReadableArray gradient) {
        mGradient = gradient;
        markUpdated();
    }

    @ReactProp(name = "gradientUnits")
    public void setGradientUnits(int gradientUnits) {
        switch (gradientUnits) {
            case 0:
                mGradientUnits = Brush.BrushUnits.OBJECT_BOUNDING_BOX;
                break;
            case 1:
                mGradientUnits = Brush.BrushUnits.USER_SPACE_ON_USE;
                break;
        }
        markUpdated();
    }

    @ReactProp(name = "gradientTransform")
    public void setGradientTransform(@Nullable ReadableArray matrixArray) {
        if (matrixArray != null) {
            int matrixSize = PropHelper.toMatrixData(matrixArray, sRawMatrix, mScale);
            if (matrixSize == 6) {
                if (mMatrix == null) {
                    mMatrix = new Matrix();
                }
                mMatrix.setValues(sRawMatrix);
            } else if (matrixSize != -1) {
                FLog.w(ReactConstants.TAG, "RNSVG: Transform matrices must be of size 6");
            }
        } else {
            mMatrix = null;
        }

        markUpdated();
    }

    @Override
    protected void saveDefinition() {
        if (mName != null) {
            WritableArray points = Arguments.createArray();
            points.pushString(mX1);
            points.pushString(mY1);
            points.pushString(mX2);
            points.pushString(mY2);

            Brush brush = new Brush(Brush.BrushType.LINEAR_GRADIENT, points, mGradientUnits);
            brush.setGradientColors(mGradient);
            if (mMatrix != null) {
                brush.setGradientTransform(mMatrix);
            }

            SvgViewShadowNode svg = getSvgShadowNode();
            if (mGradientUnits == Brush.BrushUnits.USER_SPACE_ON_USE) {
                brush.setUserSpaceBoundingBox(svg.getCanvasBounds());
            }

            svg.defineBrush(brush, mName);
        }
    }
}
