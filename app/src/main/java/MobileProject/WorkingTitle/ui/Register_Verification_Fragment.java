package MobileProject.WorkingTitle.ui;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import MobileProject.WorkingTitle.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Register_Verification_Fragment extends Fragment {


    public Register_Verification_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            String email = getArguments().getString("email");
            TextView tv = (TextView) getActivity().findViewById(R.id.registerSuccessText);
            String result = "You have successfully registered! Please check your email: " + email + "to verify your account.";
            tv.setText(result);
        } else {
            String email = "";
            TextView tv = (TextView) getActivity().findViewById(R.id.registerSuccessText);
            String result = "You have successfully registered! Please check your email " + email + "to verify your account.";
            tv.setText(result);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_register__verification_, container, false);
        View view =  inflater.inflate(R.layout.fragment_register__verification_, container, false);
        view.findViewById(R.id.button_back_to_Login).setOnClickListener(this::register);

        return view;
    }

    private void register(View view) {

        LoginFragment nextFrag= new LoginFragment();
//        Bundle arguments = new Bundle();
//        String email = ((EditText)getActivity().findViewById(R.id.editText_Email)).getText().toString();
//        arguments.putString("email",email);
//        nextFrag.setArguments(arguments);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_resister_fragment_container, nextFrag, "registerSuccessToLogin")
                .addToBackStack(null)
                .commit();
    }

}
