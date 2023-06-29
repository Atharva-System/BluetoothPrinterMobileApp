package com.samediscare.printerserverapp.data.repository

import com.brother.ptouch.sdk.LabelInfo
import com.samediscare.printerserverapp.data.CommonSpinnerModel



class LabelPrinterRepository  constructor(

) {
    fun getLanguageSelectionOptions(languageSelectionOption: ArrayList<CommonSpinnerModel>): ArrayList<CommonSpinnerModel> {
        var list: ArrayList<CommonSpinnerModel> = ArrayList()
        list.clear()
        list.add(CommonSpinnerModel(""+LabelInfo.PT.W3_5.ordinal,"3.5mm",""))
        list.add(CommonSpinnerModel(""+LabelInfo.PT.W6.ordinal,"6mm",""))
        list.add(CommonSpinnerModel(""+LabelInfo.PT.W9.ordinal,"9mm",""))
        list.add(CommonSpinnerModel(""+LabelInfo.PT.W12.ordinal,"12mm",""))
        list.add(CommonSpinnerModel(""+LabelInfo.PT.W18.ordinal,"18mm",""))
        list.add(CommonSpinnerModel(""+LabelInfo.PT.W24.ordinal,"24mm",""))
        list.add(CommonSpinnerModel(""+LabelInfo.PT.HS_W6.ordinal," HS_5.8mm",""))
        list.add(CommonSpinnerModel(""+LabelInfo.PT.HS_W9.ordinal,"HS_8.8mm",""))
        list.add(CommonSpinnerModel(""+LabelInfo.PT.HS_W12.ordinal,"HS_11.7mm",""))
        list.add(CommonSpinnerModel(""+LabelInfo.PT.HS_W18.ordinal,"HS_17.7mm",""))
        list.add(CommonSpinnerModel(""+LabelInfo.PT.HS_W24.ordinal,"HS_23.6mm",""))
        list.add(CommonSpinnerModel(""+LabelInfo.PT.W36.ordinal,"36mm",""))
        list.add(CommonSpinnerModel(""+LabelInfo.PT.FLE_W21H45.ordinal,"FL 21mm x 45mm",""))
        languageSelectionOption.addAll(list)
        return languageSelectionOption

    }

}



