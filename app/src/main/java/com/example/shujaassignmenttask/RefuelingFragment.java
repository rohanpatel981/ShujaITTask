package com.example.shujaassignmenttask;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

public class RefuelingFragment extends Fragment {

    private RecyclerView recyclerView;

    private String usersID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    ImageView imageViewAdd;
    View v;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        if (v != null) {
            if ((ViewGroup) v.getParent() != null)
                ((ViewGroup) v.getParent()).removeView(v);
            return v;
        }
        v = inflater.inflate(R.layout.fragment_refueling, container, false);

        recyclerView = v.findViewById(R.id.recycleView);
        imageViewAdd = v.findViewById(R.id.imageViewAdd);
        imageViewAdd.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddRefuel.class));
        });

        return v;
    }
}
