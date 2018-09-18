package com.julianhatzky.goodnightbaby;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class SecondFragment extends Fragment {
    View myView;
    StartRecord myRecord;
    TextView txt;
    Button record_button;
    int record_state=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView=inflater.inflate(R.layout.record, container, false);


        record_button= (Button) myView.findViewById(R.id.record_button);
        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),StartRecord.class);
                getActivity().startActivity(intent);
            }
        }) ;



        return myView;
    }


}
