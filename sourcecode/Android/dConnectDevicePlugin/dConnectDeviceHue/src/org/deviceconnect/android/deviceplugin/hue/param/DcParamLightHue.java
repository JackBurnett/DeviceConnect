/*
DcParamLightHue
Copyright (c) 2014 NTT DOCOMO,INC.
Released under the MIT license
http://opensource.org/licenses/mit-license.php
*/

package org.deviceconnect.android.deviceplugin.hue.param;

import org.deviceconnect.android.deviceplugin.param.DcParamLight;
import org.deviceconnect.android.deviceplugin.param.DcParam.DcParamException;
import org.deviceconnect.android.deviceplugin.param.DcParamLight.RgbData;
import org.deviceconnect.android.deviceplugin.util.DcLoggerHue;

import android.content.Intent;
import android.graphics.Color;

import com.philips.lighting.hue.sdk.utilities.PHUtilities;

/**
 * Hueライトパラメータ管理クラス.
 * @author NTT DOCOMO, INC.
 */
public class DcParamLightHue extends DcParamLight {
    /**
     * Max Color Value.
     */
    private static final int DEF_COLOR_VALUE = 255;
    /**
     * Tuned Color Value.
     */
    private static final int TUNED_COLOR_VALUE = 254;

    /**
     * XY管理クラス.
     */
    public static class XyData {
        /**
         * X作成.
         */
        private float x = 0;
        /**
         * Y作成.
         */
        private float y = 0;

        /**
         * X param getter.
         * 
         * @return x
         */
        public float getX() {
            return x;
        }

        /**
         * Y param getter.
         * 
         * @return y
         */
        public float getY() {
            return y;
        }
    }

    /**
     * RGBパラメータをXYデータに返還.
     * @param rgbdata RGB
     * @return XYData
     * @throws DcParamException DcParam
     */
    public static XyData getColorXYValue(final RgbData rgbdata) throws DcParamException {

        XyData xydata = new XyData();

        try {
            xydata = convRgbToXy(rgbdata);

        } catch (Exception e) {
            // 不正なリクエストパラメータエラー
            throwPramException("colorが正しくありません");
        }

        return xydata;

    }

    /**
     * .
     * 
     * @param rgbdata rgbdata
     * @return xydata
     */
    public static XyData convRgbToXy(final RgbData rgbdata) {

        DcLoggerHue logger = new DcLoggerHue();
        logger.fine("DcParamHue", "convRgbToXy RGB", rgbdata.getRedParam() + "," + rgbdata.getGreenParam() + ","
                + rgbdata.getBlueParam());

        int colorCode = Color.rgb(rgbdata.getRedParam(), rgbdata.getGreenParam(), rgbdata.getBlueParam());

        logger.fine("DcParamHue", "convRgbToXy RGB colorCode", colorCode);

        float[] xy = PHUtilities.calculateXYFromRGB(rgbdata.getRedParam(), rgbdata.getGreenParam(), rgbdata.getBlueParam(),
                "LST001");

        XyData xydata = new DcParamLightHue.XyData();

        logger.fine("DcParamHue", "convRgbToXy RGB XY", xy[0] + "," + xy[1]);

        xydata.x = xy[0];
        xydata.y = xy[1];

        return xydata;

    }

    /**
     * Hueブリッジの輝度値取得.
     * @param request request
     * @return Brightness Parameter
     * @throws DcParamException DcParam
     */
    public static int getHueBrightnessValue(final Intent request) throws DcParamException {

        int iValue = (int) (getBrightnessValue(request) * DEF_COLOR_VALUE);

        // 255を渡すとHueからエラーが来るので254に丸める
        if (iValue > TUNED_COLOR_VALUE) {
            iValue = TUNED_COLOR_VALUE;
        }
        return iValue;

    }

}