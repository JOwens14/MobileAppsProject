package MobileProject.WorkingTitle.UI.NewPassword;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import MobileProject.WorkingTitle.R;
import MobileProject.WorkingTitle.UI.Login.LoginFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordChangedFragment extends Fragment {


    public PasswordChangedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            String email = getArguments().getString("email");
            TextView tv = (TextView) getActivity().findViewById(R.id.newPWSuccessText);
            String result = "You have successfully changed your password for " + email + "!";
            tv.setText(result);
        } else {
            String email = "";
            TextView tv = (TextView) getActivity().findViewById(R.id.newPWSuccessText);
            String result = "You have successfully changed your password!";
            tv.setText(result);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_changed, container, false);
        view.findViewById(R.id.button_newPW_to_login).setOnClickListener(this::goToLogin);
        return view;
    }

    private void goToLogin(View view) {
        LoginFragment nextFrag= new LoginFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.new_pw_container, nextFrag, "newPasswordSuccessToLogin")
                .addToBackStack(null)
                .commit();
    }

}
