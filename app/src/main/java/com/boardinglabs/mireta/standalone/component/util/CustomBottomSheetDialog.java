//package com.boardinglabs.mireta.standalone.component.util;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.boardinglabs.mireta.standalone.R;
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
//
//public class CustomBottomSheetDialog extends BottomSheetDialogFragment {
//    private BottomSheetListener mListener;
////    private List<HarvestSchedule> transactionModels;
//    private Context context;
//    private Activity activity;
//
////    public CustomBottomSheetDialog(List<HarvestSchedule> transactionModels, Context context, Activity activity) {
////        this.transactionModels = transactionModels;
////        this.context = context;
////        this.activity = activity;
////    }
//
//    public CustomBottomSheetDialog (Context context) {
//        this.context = context;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.bottom_sheet_layout_event, container, false);
//        TextView tv_title = v.findViewById(R.id.title);
//        RecyclerView rv_event = v.findViewById(R.id.rvAdditionalItem);
//
////        BottomSheetEventAdapter adapter = new BottomSheetEventAdapter(context, activity);
////        rv_event.setAdapter(adapter);
//
//        return v;
//    }
//
//    public interface BottomSheetListener {
//        void onButtonClicked(String text);
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        try {
//            mListener = (BottomSheetListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement BottomSheetListener");
//        }
//    }
//}