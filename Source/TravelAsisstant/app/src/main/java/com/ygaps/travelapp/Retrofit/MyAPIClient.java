package com.ygaps.travelapp.Retrofit;

import com.ygaps.travelapp.Component.CoordinateMember;
import com.ygaps.travelapp.Component.Email;
import com.ygaps.travelapp.Component.LogInClient;
import com.ygaps.travelapp.Component.NewPassword;
import com.ygaps.travelapp.Component.RequestComment;
import com.ygaps.travelapp.Component.RequestLocation;
import com.ygaps.travelapp.Component.RequestNotifyOnRoad;
import com.ygaps.travelapp.Component.RequestSendText;
import com.ygaps.travelapp.Component.Resister;
import com.ygaps.travelapp.Component.ResponNotifyOnRoad;
import com.ygaps.travelapp.Component.ResponseNotify;
import com.ygaps.travelapp.Component.ReviewStopPoint;
import com.ygaps.travelapp.Component.ReviewTour;
import com.ygaps.travelapp.Component.StopPoint;
import com.ygaps.travelapp.Component.Tour;
import com.ygaps.travelapp.Component.User;
import com.ygaps.travelapp.Component.UserRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface MyAPIClient {

    @POST("/user/register")
    Call<User> createUser(@Body Resister resister);

    @POST("/user/login")
    Call<User> login(@Body LogInClient logInClient);

    @POST("/user/login/by-google")
    Call<User> logInByGoogle(@Body RequestLogin req);

    @POST("/user/login/by-facebook")
    Call<User> logInByFacebook(@Body RequestLogin req);

    @POST("/tour/create")
    Call<Tour> createTour(@Header("Authorization") String authorization,
                          @Body Tour tour);

    @Multipart
    @POST("/tour/update/avatar-for-tour")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image);

    @GET("/tour/list")
    Call<ResponseListTour> getListTour(@Header("Authorization") String authorization,
                                       @Query("rowPerPage") Number rowPerPage,
                                       @Query("pageNum") Number pageNum);
    @GET("/tour/history-user")
    Call<ResponseListTour> getListTourOfUser(@Header("Authorization") String authorization,
                                             @Query("pageIndex") Number rowPerPage,
                                             @Query("pageSize") String pageNum);
    @POST("/tour/set-stop-points")
    Call<ResponseBody> addStopPoints(@Header("Authorization") String authorization,
                                     @Body RequestAddStopPoints stopPoint);

    @GET("/tour/remove-stop-Point")
    Call<ResponseBody> removeStopPoint(@Header("Authorization") String author, @Query("stopPointId") Number stopPointId);

    @POST("/tour/update-stop-Point")
    Call<StopPoint> updateStopPoint(@Header("Authorization") String author, @Query("id") String tourId, @Body StopPoint stopPoint);


    @GET("/user/info")
    Call<User> getUserInfo(@Header("Authorization") String authorization);

    @POST("/user/edit-info")
    Call<User> updateUser(@Header("Authorization") String authorization, @Body UserRequest userRequest);

    @POST("/user/request-otp-recovery")
    Call<ResponRecoverPassword> recoverPassword(@Body Email email);

    @POST("/user/verify-otp-recovery")
    Call<ResponseBody> verifyNewPassword(@Body NewPassword email);

    @GET("/tour/search/service")
    Call<RequestAddStopPoints> searchDestination(@Header("Authorization") String auth,
                                                 @Query("searchKey") String keyword,
                                                 @Query("provinceId") Number provinceId,
                                                 @Query("provinceName") String provinceName,
                                                 @Query("pageIndex") Number pageIndex,
                                                 @Query("pageSize") Number pageSize);

    @POST("/user/notification/put-token")
    Call<ResponseBody> registerFireBase(@Header("Authorization") String auth,
                                        @Body RequestFireBase firebase);



    @POST("/tour/add/member")
    Call<ResponseBody> inviteMember(@Header("Authorization") String auth,
                                    @Body RequestInviteMember inviteMember);

    @GET("/tour/info")
    Call<Tour> getTourInfo(@Header("Authorization") String auth,
                           @Query("tourId") String tourId);

    @POST("/tour/suggested-destination-list")
    Call<ResponseStopPoints> getSuggestDestinations(@Header("Authorization") String auth,
                                              @Body RequestCoordList coordList);

    @GET("/tour/get/feedback-service")
    Call<ResponseFeedBackStopPoint> getListFeedbackStopPoint(@Header("Authorization") String authorization,
                                                             @Query("serviceId") Number serviceId,
                                                             @Query("pageIndex") Number rowPerPage,
                                                             @Query("pageSize") String pageNum);

    @GET("/tour/get/review-list")
    Call<ResponseFeedBackTour> getListFeedbackTour(@Header("Authorization") String authorization,
                                                   @Query("tourId") String serviceId,
                                                   @Query("pageIndex") Number rowPerPage,
                                                   @Query("pageSize") String pageNum);

    @GET("/tour/get/noti-on-road")
    Call<ResponNotifyOnRoad> getListNotifyOnRoad(@Header("Authorization") String authorization,
                                                 @Query("tourId") String serviceId,
                                                 @Query("pageIndex") Number rowPerPage,
                                                 @Query("pageSize") String pageNum);

    @GET("/tour/get/invitation")
    Call<ResponseNotify> getListNotify(@Header("Authorization") String authorization,
                                       @Query("pageIndex") Number rowPerPage,
                                       @Query("pageSize") String pageNum);


    @POST("/tour/add/review")
    Call<ResponseBody> reviewTour(@Header("Authorization") String auth,
                                  @Body ReviewTour reviewTour);

    @POST("/tour/add/feedback-service")
    Call<ResponseBody> reviewStopPoint(@Header("Authorization") String auth,
                                       @Body ReviewStopPoint reviewStopPoint);

    @POST("/tour/add/notification-on-road")
    Call<ResponseBody> notifyOnRoad(@Header("Authorization") String auth,
                                    @Body RequestNotification req);

    @POST("/tour/comment")
    Call<ResponseBody> sendCommetTour(@Header("Authorization") String auth,
                                    @Body RequestComment requestComment);

    @POST("/tour/current-users-coordinate")
    Call<List<CoordinateMember>> updateUserLocation(@Header("Authorization") String auth,
                                                    @Body RequestLocation requestLocation);


    @POST("/tour/add/notification-on-road")
    Call<ResponseBody> sendNotifyOnRoad(@Header("Authorization") String auth,
                                          @Body RequestNotifyOnRoad notifyOnRoad);

    @POST("/tour/notification")
    Call<ResponseBody> sendNotifyToATour(@Header("Authorization") String auth,
                                        @Body RequestSendText sendText);

    @POST("/tour/update-tour")
    Call<ResponseBody> updateTourInfo(@Header("Authorization") String auth,
                                      @Body Tour tour);

    @POST("/tour/response/invitation")
    Call<ResponseBody> responseInvitation(@Header("Authorization") String auth,
                                          @Body RequestAcceptInvitation req);

}
