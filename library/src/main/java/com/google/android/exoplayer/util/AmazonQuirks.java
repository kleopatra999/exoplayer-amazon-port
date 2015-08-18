/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer.util;

import java.lang.String;

import android.os.Build;

import com.google.android.exoplayer.util.Util;
import com.google.android.exoplayer.util.MimeTypes;
import android.util.Log;
 public class AmazonQuirks {
  private static final String FIRETV_GEN1_DEVICE_MODEL = "AFTB";
  private static final String FIRETV_STICK_DEVICE_MODEL = "AFTM";
  private static final String FIRETV_GEN2_DEVICE_MODEL = "AFTS";
  private static final String AMAZON = "Amazon";
  private static final String DEVICEMODEL = Build.MODEL;
  private static final String MANUFACTURER = Build.MANUFACTURER;
  private static final int AUDIO_HARDWARE_LATENCY_FOR_TABLETS = 90000;
  // The audio format values for Dolby passthrough in Fire TV (Gen 1) family
  // is different than the ones defined in API 21.
  public static final int AUDIO_FORMAT_LEGACY_ENCODING_AC3 = 107;
  public static final int AUDIO_FORMAT_LEGACY_ENCODING_EAC3 = 108;

  public static boolean isAdaptive(String mimeType) {
    if (mimeType == null || mimeType.isEmpty()) {
      return false;
    }
    // Fire TV and tablets till now support adaptive codecs by default for video
    return ( isAmazonDevice() &&
             (mimeType.equalsIgnoreCase(MimeTypes.VIDEO_H264) ||
                mimeType.equalsIgnoreCase(MimeTypes.VIDEO_MP4)) );
  }

  public static boolean isLatencyQuirkEnabled() {
    // Sets latency quirk for Amazon KK and JB Tablets
    return ( (Util.SDK_INT <= 19) &&
             isAmazonDevice() && (!isFireTVFamily()) );
  }

  public static int getAudioHWLatency() {
    // this function is called only when the above function
    // returns true for latency quirk. So no need to check for
    // SDK version and device type again
    return AUDIO_HARDWARE_LATENCY_FOR_TABLETS;
  }

  public static boolean isDolbyPassthroughQuirkEnabled() {
    // Sets dolby passthrough quirk for Amazon Fire TV (Gen 1) Family
    return isAmazonDevice() && isFireTVFamily();
  }

  public static boolean isAc3(int encoding) {
    return ( isDolbyPassthroughQuirkEnabled() &&
             (encoding == AUDIO_FORMAT_LEGACY_ENCODING_AC3 ||
                encoding == AUDIO_FORMAT_LEGACY_ENCODING_EAC3) );
  }

  public static boolean isAmazonDevice(){
    return MANUFACTURER.equalsIgnoreCase(AMAZON);
  }

  public static boolean isFireTVFamily() {
    Log.d("AMZNQUIRK",DEVICEMODEL);
    //TODO: should probably also check isAmazonDevice
    //Note: don't put gen2 here as it will enable dobly quirks for it!
    return ( DEVICEMODEL.equalsIgnoreCase(FIRETV_GEN1_DEVICE_MODEL)
            || DEVICEMODEL.equalsIgnoreCase(FIRETV_STICK_DEVICE_MODEL) );
  }
  public static boolean isDecoderBlacklisted(String codecName) {
     if(!isAmazonDevice()) {
         return false;
     }
     if(DEVICEMODEL.equalsIgnoreCase(FIRETV_GEN2_DEVICE_MODEL)
             && codecName.startsWith("OMX.MTK.AUDIO.DECODER.MP3")) {
         return true;
     }
     return false;
  }

 }
