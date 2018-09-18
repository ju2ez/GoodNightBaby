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
import android.widget.Toast;

public class FirstFragment extends Fragment {
    View myView;
    Button sleepmode_button;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView=inflater.inflate(R.layout.sleep_mode, container, false);

        sleepmode_button= (Button) myView.findViewById(R.id.sleepmode_button);
        sleepmode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SleepMode.class);
                if(MainActivity.pathSave.length()>10)
                    getActivity().startActivity(intent);
                else
                    Toast.makeText(getActivity(), "Du musst erst etwas aufnehmen..", Toast.LENGTH_SHORT).show();
            }
        }) ;

        return myView;
    }
}
