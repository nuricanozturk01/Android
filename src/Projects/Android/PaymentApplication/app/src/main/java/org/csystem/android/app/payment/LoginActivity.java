package org.csystem.android.app.payment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.karandev.util.data.service.DataServiceException;

import org.csystem.android.app.payment.data.service.PaymentApplicationDataService;
import org.csystem.android.app.payment.data.service.dto.LoginInfoDTO;
import org.csystem.android.app.payment.databinding.ActivityLoginBinding;
import static org.csystem.android.app.payment.global.key.BundleKeyKt.LOGIN_INFO;
import org.csystem.android.app.payment.viewmodel.LoginActivityListenerViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding m_binding;

    @Inject
    PaymentApplicationDataService datService;

    private void initBinding()
    {
        m_binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        m_binding.setViewModel(new LoginActivityListenerViewModel(this));
        m_binding.setLoginInfo(new LoginInfoDTO());
    }

    private void init()
    {
        initBinding();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        init();
    }

    public void loginButtonClicked()
    {
        try {
            var loginInfo = m_binding.getLoginInfo();
            if (datService.checkAndSaveLoginInfo(loginInfo)) {
                Toast.makeText(this, "Access granted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, OperationsActivity.class).putExtra(LOGIN_INFO, loginInfo));
            }
            else
                Toast.makeText(this, "Access denied!...", Toast.LENGTH_SHORT).show();
        }
        catch (DataServiceException ex) {
            Toast.makeText(this, "Data problem:" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        catch (Throwable ignore) {
            Toast.makeText(this, "Problem occurred. Try again later", Toast.LENGTH_LONG).show();
        }
    }
}