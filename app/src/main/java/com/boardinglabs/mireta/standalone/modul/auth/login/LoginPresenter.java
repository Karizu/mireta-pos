package com.boardinglabs.mireta.standalone.modul.auth.login;

import com.boardinglabs.mireta.standalone.component.network.ResponeError;
import com.boardinglabs.mireta.standalone.component.network.response.LoginResponse;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.CommonInterface;

import rx.Subscriber;

public class LoginPresenter {
    private CommonInterface cInterface;
    private LoginView mView;
    private LoginInteractor mInteractor;

    public LoginPresenter(CommonInterface cInterface, LoginView view) {
        mView = view;
        this.cInterface = cInterface;
        mInteractor = new LoginInteractor(this.cInterface.getService());
    }

    public void login(String username, String password) {
        cInterface.showProgressLoading();
        mInteractor.login(username, password).subscribe(new Subscriber<LoginResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                cInterface.hideProgresLoading();
                cInterface.onFailureRequest(ResponeError.getErrorMessage(e));
            }

            @Override
            public void onNext(LoginResponse loginResponse) {
                cInterface.hideProgresLoading();
                PreferenceManager.saveLogIn(loginResponse.access_token, loginResponse.data.id, loginResponse.data.fullname, loginResponse.data.username);
                PreferenceManager.saveUser(loginResponse.data);
                PreferenceManager.saveBusiness(loginResponse.data.business);
                PreferenceManager.saveStockLocation(loginResponse.data.user_location);
                PreferenceManager.saveRole(loginResponse.data.role);
                mView.onSuccessRequest();
            }
        });
    }
}
