/*
 * Copyright (c) 2016. The Android Open Source Project
 * Created by idisfkj
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.avit.xtvlibs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.avit.xtvlibs.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 
 * @author laird
 * @date 2019/5/23 15:14
 *
 */
public class FourPicker<T extends Object> extends LinearLayout {
    private TextView pickerTitle;
    private PickerViewTV PickerViewTV1;
    private PickerViewTV PickerViewTV2;
    private PickerViewTV PickerViewTV3;
    private PickerViewTV PickerViewTV4;

    private T leftText;
    private T middleText1;
    private T middleText2;
    private T rightText;
    
    private List<T> leftList;
    private List<T> middleList1;
    private List<T> middleList2;
    private List<T> rightList;
    private int selectedPosition1 = -1;
    private int selectedPosition2 = -1;
    private int selectedPosition3 = -1;
    private int selectedPosition4 = -1;
    
    private SelectedFinishListener mListener;


    public FourPicker(Context context) {
    	super(context);
        init(context);
    }
    
    public FourPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
        View view  = LayoutInflater.from(context).inflate(R.layout.fourpicker,this,true);
        PickerViewTV1 = (PickerViewTV) view.findViewById(R.id.picker_view1);
        PickerViewTV2 = (PickerViewTV) view.findViewById(R.id.picker_view2);
        PickerViewTV3 = (PickerViewTV) view.findViewById(R.id.picker_view3);
        PickerViewTV4 = (PickerViewTV) view.findViewById(R.id.picker_view4);
        PickerViewTV1.setOnSelectorListener(new PickerViewTV.OnSelectorListener() {
            @Override
            public void onSelector(String text) {
                leftText = (T) text;
                mListener.onFinish();
            }
        });
        PickerViewTV2.setOnSelectorListener(new PickerViewTV.OnSelectorListener() {
            @Override
            public void onSelector(String text) {
                middleText1 = (T) text;
                mListener.onFinish();
            }
        });
        PickerViewTV3.setOnSelectorListener(new PickerViewTV.OnSelectorListener() {
            @Override
            public void onSelector(String text) {
            	middleText2 = (T) text;
                mListener.onFinish();
            }
        });
        PickerViewTV4.setOnSelectorListener(new PickerViewTV.OnSelectorListener() {
            @Override
            public void onSelector(String text) {
                rightText = (T) text;
                mListener.onFinish();
            }
        });
        leftList = new ArrayList<T>();
        middleList1 = new ArrayList<T>();
        middleList2= new ArrayList<T>();
        rightList = new ArrayList<T>();

        this.setFocusable(true);
    }

    /**
     * default show three
     * @param num
     */
    public void setShowNum(int num){
        switch (num){
            case 1:
                PickerViewTV2.setVisibility(View.GONE);
                PickerViewTV3.setVisibility(View.GONE);
                break;
            case 2:
                PickerViewTV3.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


    /**
     * Fill in the data
     * @param dataList List
     * @param i Select the picker
     *          1-first  2-second  3-third
     */
    public void setData(List<T> dataList, int i) {
        switch (i) {
            case 1:
                leftList = dataList;
                PickerViewTV1.setData(leftList);
                break;
            case 2:
                middleList1 = dataList;
                PickerViewTV2.setData(middleList1);
                break;
            case 3:
                middleList2 = dataList;
                PickerViewTV3.setData(middleList2);
                break;
            case 4:
                rightList = dataList;
                PickerViewTV4.setData(rightList);
                break;
        }
    }

    /**
     * set the default centered text,
     * if not set,show centered in the data
     * @param position position in the data
     * @param i  Select the picker
     *          1-first  2-second  3-third
     */
    public void setMiddleText(int position, int i) {
        switch (i) {
            case 1:
                PickerViewTV1.setSelected(position);
                selectedPosition1 = position;
                break;
            case 2:
                PickerViewTV2.setSelected(position);
                selectedPosition2 = position;
                break;
            case 3:
                PickerViewTV3.setSelected(position);
                selectedPosition3 = position;
                break;
            case 4:
                PickerViewTV4.setSelected(position);
                selectedPosition4 = position;
                break;
        }
    }

    /**
     * Access to select text
     * @param i Select the picker
     *          1-first  2-second  3-third
     * @return String
     */
    public T getText(int i) {
        T text = null;
        switch (i) {
            case 1:
                text = leftText;
                break;
            case 2:
                text = middleText1;
                break;
            case 3:
                text = middleText2;
                break;
            case 4:
                text = rightText;
                break;
        }
        return text;
    }



    /**
     * redy
     */
    public void setPrepare() {
        if (selectedPosition1 != -1) {
            leftText = leftList.get(selectedPosition1);
        } else {
            leftText = leftList.get(leftList.size() / 2);
        }

        if (selectedPosition2 != -1) {
            middleText1 = middleList1.get(selectedPosition2);
        } else {
            middleText1 = middleList1.get(middleList1.size() / 2);
        }

        if (selectedPosition3 != -1) {
        	middleText2 = middleList2.get(selectedPosition4);
        } else {
        	middleText2 = middleList2.get(middleList2.size() / 2);
        }
        if (selectedPosition4 != -1) {
            rightText = rightList.get(selectedPosition4);
        } else {
            rightText = rightList.get(rightList.size() / 2);
        }
        

    }
    
    public void setSelectedFinishListener(SelectedFinishListener listener){
        mListener = listener;
    }

    public interface SelectedFinishListener {
        void onFinish();
    }
}
