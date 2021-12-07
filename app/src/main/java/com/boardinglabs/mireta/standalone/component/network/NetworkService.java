package com.boardinglabs.mireta.standalone.component.network;

import com.boardinglabs.mireta.standalone.component.network.entities.Ardi.Booths;
import com.boardinglabs.mireta.standalone.component.network.entities.Ardi.CheckMemberResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Ardi.HistoryTopup.HistoryTopup;
import com.boardinglabs.mireta.standalone.component.network.entities.Ardi.Members;
import com.boardinglabs.mireta.standalone.component.network.entities.Ardi.Topup;
import com.boardinglabs.mireta.standalone.component.network.entities.Ardi.Trx.TransactionArdi;
import com.boardinglabs.mireta.standalone.component.network.entities.Ardi.Users;
import com.boardinglabs.mireta.standalone.component.network.entities.CategoriesResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.Locations.DetailLocationResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.SecurityQuestions;
import com.boardinglabs.mireta.standalone.component.network.entities.Stocks.StockResponse;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionPost;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionToCashier;
import com.boardinglabs.mireta.standalone.component.network.response.ApiResponse;
import com.boardinglabs.mireta.standalone.component.network.response.ItemsResponse;
import com.boardinglabs.mireta.standalone.component.network.response.LoginResponse;
import com.boardinglabs.mireta.standalone.component.network.response.TransactionListResponse;
import com.boardinglabs.mireta.standalone.modul.master.brand.model.CategoryModel;
import com.boardinglabs.mireta.standalone.modul.master.laporan.model.LaporanResponse;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.model.Item;
import com.boardinglabs.mireta.standalone.modul.master.stok.inventori.model.ItemResponse;
import com.google.gson.JsonObject;
import com.boardinglabs.mireta.standalone.component.network.gson.GAgent;
import com.boardinglabs.mireta.standalone.component.network.gson.GBalance;
import com.boardinglabs.mireta.standalone.component.network.gson.GBanks;
import com.boardinglabs.mireta.standalone.component.network.gson.GCard;
import com.boardinglabs.mireta.standalone.component.network.gson.GCashback;
import com.boardinglabs.mireta.standalone.component.network.gson.GCashbackRedeem;
import com.boardinglabs.mireta.standalone.component.network.gson.GCreditCard;
import com.boardinglabs.mireta.standalone.component.network.gson.GLogo;
import com.boardinglabs.mireta.standalone.component.network.gson.GMerchant;
import com.boardinglabs.mireta.standalone.component.network.gson.GPromo;
import com.boardinglabs.mireta.standalone.component.network.gson.GRedeem;
import com.boardinglabs.mireta.standalone.component.network.gson.GReward;
import com.boardinglabs.mireta.standalone.component.network.gson.GTopup;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.AgentResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.BankTransferResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.CalculateResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.CashbackResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.CreditCardResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.DealsResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.MerchantResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.MessageResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.ParkingResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.PostsResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.PromoResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.QRTransactionResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.ServicesResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.SinglePostResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.SyncContactResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.TopupResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.TransactionHistoryResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.TransactionParkingResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.TransactionResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.TransferRequestLogGroupResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.TransferRequestLogResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.TransferRequestResponse;
import com.boardinglabs.mireta.standalone.component.network.oldresponse.VoucherResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

public interface NetworkService {

    String BASE_URL = "http://54.255.97.130/mireta-selada/public/api/";

//    String BASE_NEW_URL_LOCAL = "http://192.168.1.9/mireta-pos/public/api/";
//        String BASE_NEW_URL_LOCAL = "http://37.72.172.144/mireta-selada/public/api/";
    String BASE_MIRETA_DEV = "http://36.94.58.181/api/mireta-api/public/index.php/api/";
    String BASE_MIRETA_PROD = "http://36.94.58.178/api/mireta-api/public/index.php/api/";
    String BASE_URL_IMAGE = "http://36.94.58.178/api/mireta-api";
    String BASE_NEW_URL_LOCAL = BASE_MIRETA_PROD;
    //    String BASE_NEW_URL_LOCAL = "http://37.72.172.144/mireta-pos/public/api/";
//    String BASE_ARDI = "http://192.168.1.9/ardi-api/public/api/";

    String BASE_ARDI_DEV = "http://36.94.58.181/api/ardi-api/public/index.php/api/";
    String BASE_ARDI_PROD = "http://36.94.58.178/api/ardi-api/public/index.php/api/";
    String BASE_ARDI = BASE_ARDI_PROD;
//    String BASE_ARDI = "http://37.72.172.144/ardi-api/public/api/";

    @FormUrlEncoded
    @POST("customers")
    Observable<MessageResponse> register(@Field("name") String name,
                                         @Field("mobile") String mobile,
                                         @Field("email") String email,
                                         @Field("referral_id") String refferalId
    );

    @FormUrlEncoded
    @PUT("customers/info")
    Observable<GAgent> updateProfile(@Field("name") String name,
                                     @Field("mobile") String mobile,
                                     @Field("email") String email,
                                     @Field("username") String username,
                                     @Field("last_name") String last_name,
                                     @Field("gender") String gender,
                                     @Field("address") String address,
                                     @Field("birthdate") String birthdate,
                                     @Field("avatar_base64") String avatar_base64);

    @FormUrlEncoded
    @POST("customers/verify")
    Observable<AgentResponse> verifyAgent(@Field("mobile") String mobile,
                                          @Field("code") String code);

    @FormUrlEncoded
    @PUT("customers/passcode")
    Observable<MessageResponse> setPasscode(@Field("passcode") String passcode,
                                            @Field("passcode_confirmation") String passcodeConfirm);

    @GET("customers/passcode/{nomormobile}")
    Observable<MessageResponse> resendCode(@Path("nomormobile") String mobile);

    @FormUrlEncoded
    @POST("customers/auth")
    Observable<AgentResponse> login(@Field("mobile") String mobile,
                                    @Field("passcode") String passcode,
                                    @Field("device_active") String imei);

    @GET("customers/balance")
    Observable<GBalance> getBalance();

    @FormUrlEncoded
    @POST("topup")
    Observable<GTopup> topupBalance(@Field("amount") String amount,
                                    @Field("voucher_id") String voucherId,
                                    @Field("method_payment") String method);

    @GET("banks")
    Observable<List<GBanks>> getBanks();

    @FormUrlEncoded
    @POST("topup/transfer")
    Observable<MessageResponse> transferBalance(@Field("mobile") String mobile,
                                                @Field("amount") String amount,
                                                @Field("notes") String notes);

    @FormUrlEncoded
    @POST("topup/transferByRequest")
    Observable<MessageResponse> transferBalanceByRequest(@Field("request_id") String mobile,
                                                         @Field("accept") String amount);

    @FormUrlEncoded
    @POST("topup/request")
    Observable<MessageResponse> requestBalance(@Field("to_customer_id") String destCustId,
                                               @Field("amount") String amount,
                                               @Field("notes") String notes);

    @FormUrlEncoded
    @PUT("topup/confirm")
    Observable<BankTransferResponse> bankTransfer(@Field("order_id") String orderId,
                                                  @Field("bank_id") String bankId,
                                                  @Field("account_id") String accountId);

    @GET("vouchers")
    Observable<VoucherResponse> checkVoucher(@Query("code") String code,
                                             @Query("amount") String amount);

    @GET("customers/{code}")
    Observable<AgentResponse> checkRefferalCode(@Path("code") String code);

    @GET("customers/premium")
    Observable<JsonObject> checkPremium();

    @FormUrlEncoded
    @POST("premiums/subscribe")
    Observable<MessageResponse> subscribePremium(@Field("referral_id") String refferalId);

    @GET("services")
    Observable<ServicesResponse> getService(@Query("type") String type,
                                            @Query("amount") String amount,
                                            @Query("customer_no") String customerNo,
                                            @Query("category") String cat);

    @FormUrlEncoded
    @POST("transactionsppob/pay")
    Observable<TransactionResponse> payTransaction(@Field("transaction_id") String transactionId);

    @FormUrlEncoded
    @POST("/transactionsppob/pay")
    Observable<TransactionResponse> payTransactionJiwasraya(@Field("transaction_id") String transactionId,
                                                            @Field("bill_code") String billCode);

    @FormUrlEncoded
    @POST("transactionsppob/inquiry")
    Observable<TransactionResponse> setInquiry(@Field("customer_no") String customerNo,
                                               @Field("service_id") String serviceId,
                                               @Field("amount") String amount);

    @FormUrlEncoded
    @POST("/transactionsppob/updateamount")
    Observable<TransactionResponse> updateAmountInquiry(@Field("transaction_id") String transactionId,
                                                        @Field("amount") String amount);

    @FormUrlEncoded
    @POST("transactionsppob")
    Observable<TransactionResponse> getTransactionWithoutInquiry(@Field("customer_no") String customerNo,
                                                                 @Field("service_id") String serviceId);

    @GET("cashbacks")
    Observable<GCashback> getCashback();

    @GET("customers/total_downlines")
    Observable<JsonObject> getTotalDownline();

    @GET("rewards")
    Observable<List<GReward>> getReward();

    @GET("customers/transactions")
    Observable<TransactionHistoryResponse> getTransaction(@Query("page") int page);

    @GET("customers/transactionsppob")
    Observable<TransactionHistoryResponse> getTransactionPPOB(@Query("page") int page);

    @POST("cashbacks/redeem")
    Observable<GCashbackRedeem> redeemCashback();

    @FormUrlEncoded
    @POST("rewards/redeem")
    Observable<GRedeem> redeemReward(@Field("rewards_id") String rewardId);

    @Multipart
    @POST("uploads/image")
    Observable<List<GLogo>> uploadChat(@Part("ref") RequestBody agents,
                                       @Part("type") RequestBody chat,
                                       @Part MultipartBody.Part file);

    @Multipart
    @POST("uploads/image")
    Observable<List<GLogo>> uploadChat(@Part MultipartBody.Part file);

    @Multipart
    @POST("uploads/image")
    Observable<List<GLogo>> uploadChat(@Part("ref") RequestBody ref,
                                       @Part("type") RequestBody type,
                                       @Part("file[]") RequestBody file);

    @GET("customers/topups")
    Observable<TopupResponse> getTopupTransaction(@Query("page") int page);

    @GET("customers/cashbacks")
    Observable<CashbackResponse> getCashbackList(@Query("page") int page);

    @FormUrlEncoded
    @PUT("topup/bank")
    Observable<BankTransferResponse> updateBank(@Field("order_id") String orderId,
                                                @Field("bank_id") String bankId,
                                                @Field("account_id") String accountId);

    @GET("transactions/qr/{code}")
    Observable<QRTransactionResponse> getQrData(@Path("code") String code);

    @POST("transactions/midtrans")
    Observable<GCreditCard> postTransactionRecipientMidtrans(@Body RequestBody body);

    @FormUrlEncoded
    @POST("transactions/confirm")
    Observable<QRTransactionResponse> transactionConfirm(@Field("transaction_id") String transactionId);

    @FormUrlEncoded
    @POST("customers/cards")
    Observable<JsonObject> attachCreditCard(@Field("card") String tokenId);

    @FormUrlEncoded
    @POST("customers/cards")
    Observable<JsonObject> addCreditCard(@Query("provider") String provider,
                                         @Field("card_token") String tokenId,
                                         @Field("cardhash") String cardHash,
                                         @Field("card_name") String cardName,
                                         @Field("expiry_month") String expiryMonth,
                                         @Field("expiry_year") String expiryYear,
                                         @Field("card_expiry_at") String expireToken);

    @FormUrlEncoded
    @POST("customers/cards/setdefault")
    Observable<CreditCardResponse> setDefaultCard(@Field("token") String token);

    @FormUrlEncoded
    @POST("transactions/calculate_charge")
    Observable<CalculateResponse> calculateAmount(@Field("amount") String amount,
                                                  @Field("card_type") String cardType,
                                                  @Field("merchant_id") String merchantId);

    @GET("customers/qr")
    Observable<GLogo> getQr();

    @GET("merchants/{code}")
    Observable<GMerchant> getMerchantDetail(@Path("code") String code);

    @GET("merchants")
    Observable<MerchantResponse> getMerchantList(@Query("page") int page,
                                                 @Query("q") String search);

    @GET("customers/cards/")
    Observable<List<GCard>> getCreditCardMidtransList(@Query("provider") String provider);

    @GET("customers/cards")
    Observable<List<GCard>> getCreditCardList();

    @DELETE("customers/cards/{card_id}")
    Observable<JsonObject> deleteCard(@Path("card_id") String card_id);

    @DELETE("customers/cards/{card_id}")
    Observable<JsonObject> deleteCardMidtrans(@Path("card_id") String card_id,
                                              @Query("provider") String provider);

    @GET("content/promo")
    Observable<PromoResponse> getPromo();

    @GET("content/promo/{promo_id}")
    Observable<GPromo> getPromoDetail(@Path("promo_id") String promo_id);

    @FormUrlEncoded
    @POST("transactionparkings")
    Observable<ParkingResponse> startParking(@Field("merchant_id") String merchantId,
                                             @Field("type_vehicle") String typeVehicle);

    @GET("merchants/parking_qr/{code}")
    Observable<JsonObject> startParkingByCode(@Path("code") String code);

    @FormUrlEncoded
    @PUT("transactionparkings/update_ticket/{transaction_id}")
    Observable<TransactionParkingResponse> finishParking(@Path("transaction_id") String transaction_id,
                                                         @Field("ticket_no") String ticket_no);

    @FormUrlEncoded
    @POST("deals/redeem")
    Observable<JsonObject> redeem(@Field("deal_id") String dealId);

    @GET("deals")
    Observable<DealsResponse> getDeals();

    @FormUrlEncoded
    @PUT("customers/logout")
    Observable<JsonObject> logout(@Field("customer_id") String costumerId);

    @FormUrlEncoded
    @POST("topup/request")
    Observable<TransferRequestResponse> request(@Field("to_customer_id") String to_customer_id,
                                                @Field("amount") String amount);

    @FormUrlEncoded
    @POST("customers/syncContact")
    Observable<SyncContactResponse> syncContact(@Field("mobiles") String mobiles);

    @GET("posts/index")
    Observable<PostsResponse> fetchFeed(@Query("page") int page);

    @FormUrlEncoded
    @POST("customers/invite")
    Observable<MessageResponse> invite(@Field("mobiles") String mobiles);

    @GET("posts/detail/{post_id}")
    Observable<SinglePostResponse> getPostDetail(@Path("post_id") String post_id);

    @FormUrlEncoded
    @POST("posts/addComment/{post_id}")
    Observable<SinglePostResponse> addComment(@Path("post_id") String post_id, @Field("full_post_return") boolean full_post_return, @Field("text") String text);

    @FormUrlEncoded
    @POST("posts/deleteComment/{comment_id}")
    Observable<SinglePostResponse> deleteComment(@Path("post_id") String comment_id, @Field("full_post_return") boolean full_post_return);

    @GET("posts/deletePost/{post_id}")
    Observable<MessageResponse> deletePost(@Path("post_id") String post_id);

    @FormUrlEncoded
    @POST("posts/like/{post_id}")
    Observable<SinglePostResponse> likeDislikePost(@Path("post_id") String post_id, @Field("full_post_return") boolean full_post_return);

    @FormUrlEncoded
    @POST("posts/create")
    Observable<SinglePostResponse> createPost(@Field("text") String text);

    @GET("topup/transferRequetsLog/{to_customer_id}")
    Observable<TransferRequestLogResponse> getTransferRequestLog(@Path("to_customer_id") String to_customer_id);

    @GET("topup/transferRequetsLogGroups")
    Observable<TransferRequestLogGroupResponse> getTransferRequestLogGroup();

/////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////

    @FormUrlEncoded
    @POST("auth/login")
    Observable<LoginResponse> loginBusiness(@Field("username") String username,
                                            @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/login")
    Call<LoginResponse> checkPass(@Field("username") String username,
                                            @Field("password") String password);

    @GET("transactions")
    Observable<TransactionListResponse> getTransactions(@Query("stock_location_id") String stockLocationId,
                                                        @Query("with_details") boolean withDetails,
                                                        @Header("Authorization") String token);

    @GET("stocks")
    Observable<ItemsResponse> getStockItems(@Query("location_id") String location_id,
                                            @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("transactions")
    Observable<ResponseBody> createTransaction(@Body TransactionPost transactionPost,
                                               @Header("Authorization") String token);

    @POST("transactions")
    Call<ApiResponse<com.boardinglabs.mireta.standalone.component.network.entities.Trx.TransactionResponse>> createTransactions(@Body TransactionPost transactionPost,
                                                                                                                                @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("transaction")
    Call<ApiResponse<com.boardinglabs.mireta.standalone.modul.transactions.items.pembayaran.model.TransactionResponse>> createTransactionToCashier(@Body TransactionToCashier transactionToCashier,
                                                                                                                                                   @Header("Authorization") String token);

    @GET("transactions")
    Observable<List<com.boardinglabs.mireta.standalone.component.network.entities.TransactionResponse>> getLastTransactionNow(@Query("status") String status,
                                                                                                                              @Query("business_id") String business_id,
                                                                                                                              @Header("Authorization") String token);

    @GET("transactions")
    Call<ApiResponse<List<com.boardinglabs.mireta.standalone.component.network.entities.Trx.Transactions>>> getLastTransaction(@Query("status") String status,
                                                                                                                               @Query("location_id") String location_id,
                                                                                                                               @Query("date") String order_date,
                                                                                                                               @Header("Authorization") String token);

    @GET("transactions")
    Call<ApiResponse<List<com.boardinglabs.mireta.standalone.component.network.entities.Trx.Transactions>>> getMonthTransaction(@Query("status") String status,
                                                                                                                                @Query("location_id") String location_id,
                                                                                                                                @Query("month") String month,
                                                                                                                                @Query("year") String year,
                                                                                                                                @Header("Authorization") String token);

    @GET("transactions")
    Call<ApiResponse<List<com.boardinglabs.mireta.standalone.component.network.entities.Trx.Transactions>>> getHistory(@Query("location_id") String location_id,
                                                                                                                       @Header("Authorization") String token);

    @GET("transactions")
    Call<ApiResponse<List<com.boardinglabs.mireta.standalone.component.network.entities.Trx.Transactions>>> getHistoryToday(@Query("location_id") String location_id,
                                                                                                                            @Query("status") String status,
                                                                                                                            @Query("date") String order_date,
                                                                                                                            @Header("Authorization") String token);

    @GET("transactions/{transaction_id}")
    Call<ApiResponse<com.boardinglabs.mireta.standalone.component.network.entities.Trx.Transactions>> getDetailTransaction(@Path("transaction_id") String transaction_id,
                                                                                                                           @Header("Authorization") String token);

    @GET("stocks")
    Call<ApiResponse<List<StockResponse>>> getKatalogStok(@Query("location_id") String location_id,
                                                          @Query("item_id") String item_id,
                                                          @Header("Authorization") String token);

    @GET("items")
    Call<ApiResponse<List<com.boardinglabs.mireta.standalone.component.network.entities.Items.ItemResponse>>> getListKatalog(@Query("business_id") String business_id,
                                                                                                                             @Header("Authorization") String token);

    @GET("categories/{categories_id}")
    Call<ApiResponse<com.boardinglabs.mireta.standalone.component.network.entities.Items.ItemResponse>> getDetailKategori(@Path("categories_id") String categories_id,
                                                                                                                          @Header("Authorization") String token);


    @POST("items")
    Call<ApiResponse<ItemResponse>> createItem(@Body RequestBody requestBody,
                                               @Header("Authorization") String token);

    @POST("items/{id}")
    Call<ApiResponse<ItemResponse>> updateKategori(@Path("id") String id,
                                                   @Body RequestBody requestBody,
                                                   @Header("Authorization") String token);

    @GET("categories")
    Call<ApiResponse<List<CategoryModel>>> getListCategory(@Query("business_id") String business_id,
                                                           @Query("category_id") String id_category_nested,
                                                           @Header("Authorization") String token);


    @POST("items/{id}")
    Call<ApiResponse> postImageItem(@Path("id") String id, @Body RequestBody requestBody, @Header("Authorization") String token);

    @POST("categories")
    Call<ApiResponse> postCategories(@Body RequestBody requestBody, @Header("Authorization") String token);

    @POST("categories")
    Call<ApiResponse<CategoriesResponse>> postCategoriesInCreateItem(@Body RequestBody requestBody, @Header("Authorization") String token);

    @POST("categories/{id}")
    Call<ApiResponse<CategoryModel>> kelolaCategories(@Path("id") String id, @Body RequestBody requestBody, @Header("Authorization") String token);

    @DELETE("categories/{id}")
    Call<ApiResponse<CategoryModel>> deleteCategories(@Path("id") String id, @Header("Authorization") String token);


    @POST("items/{id}")
    Call<ApiResponse<ItemResponse>> showIsDailyStok(@Path("id") String id, @Body RequestBody requestBody, @Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("items/{id}")
    Call<ApiResponse<ItemResponse>> updateStok(@Path("id") String id, @Body Item item, @Header("Authorization") String token);

    @GET("laporan")
    Call<ApiResponse<List<LaporanResponse>>> getLaporanStock(@Query("stock_location_id") String stock_location_id,
                                                             @Header("Authorization") String token);

    // New API

    @POST("auth/register")
    Call<ApiResponse> registerUserArdi(@Body RequestBody requestBody);

    @POST("auth/regBusiness")
    Call<ApiResponse> registerUser(@Body RequestBody requestBody,
                                   @Header("Authorization") String token);

    @POST("locations/{location_id}")
    Call<ApiResponse> updateProfilToko(@Path("location_id") String location_id, @Body RequestBody requestBody,
                                       @Header("Authorization") String token);

    @POST("locations/{id}")
    Call<ApiResponse<DetailLocationResponse>> getDetailLocation(@Path("id") String id,
                                                                @Header("Authorization") String token);

    @DELETE("items/{id}")
    Call<ApiResponse> deleteItems(@Path("id") String id,
                                  @Header("Authorization") String token);

    @POST("items/{id}")
    Call<ApiResponse> updateItem(@Path("id") String id, @Body RequestBody requestBody,
                                 @Header("Authorization") String token);

    @POST("stocks")
    Call<ApiResponse> createStock(@Body RequestBody requestBody,
                                  @Header("Authorization") String token);

    @POST("stocks/{stock_id}")
    Call<ApiResponse> updateStock(@Path("stock_id") String stock_id,
                                  @Body RequestBody requestBody,
                                  @Header("Authorization") String token);

    @POST("auth/changePassword")
    Call<ApiResponse> changePassword(@Body RequestBody requestBody,
                                     @Header("Authorization") String token);

    @GET("transactions/report")
    Call<ApiResponse> getReport(@Query("is_settled") String is_settled,
                                @Query("location_id") String location_id,
                                @Header("Authorization") String token);

    @POST("auth/forgot")
    Call<ApiResponse> postForgotPassword(@Body RequestBody requestBody);

    @POST("auth/recover")
    Call<ApiResponse> postRecoverPassword(@Body RequestBody requestBody);

    @GET("members/{member_id}")
    Call<ApiResponse<Members>> cekSaldo(@Path("member_id") String member_id,
                                        @Query("partnerID") String partner_id,
                                        @Header("Authorization") String token);

    @POST("transactions")
    Call<ApiResponse<TransactionArdi>> postTransactionArdi(@Body RequestBody requestBody,
                                                           @Header("Authorization") String token);

    @POST("auth/login")
    Call<ApiResponse<com.boardinglabs.mireta.standalone.component.network.entities.LoginResponse>> loginArdi(@Body RequestBody requestBody);


    @POST("topups")
    Call<ApiResponse<Topup>> doTopup(@Body RequestBody requestBody,
                                     @Header("Authorization") String token);

    @GET("booths")
    Call<ApiResponse<List<Booths>>> getListBooth(@Header("Authorization") String token);

    @GET("users")
    Call<ApiResponse<List<Users>>> getListUsers(@Header("Authorization") String token);

    @GET("topups")
    Call<ApiResponse<List<HistoryTopup>>> getListTopup(@Query("booth_id") String booth_id,
                                                       @Header("Authorization") String token);

    @GET("topups")
    Call<ApiResponse<List<HistoryTopup>>> getListTopupWithDate(@Query("booth_id") String booth_id,
                                                               @Query("date") String date,
                                                               @Header("Authorization") String token);

    @POST("booths/{booth_id}/register")
    Call<ApiResponse> updateBoothStatus(@Path("booth_id") String booth_id,
                                        @Body RequestBody requestBody,
                                        @Header("Authorization") String token);

    @POST("reversal")
    Call<ApiResponse> sendReversal(@Body RequestBody requestBody,
                                   @Header("Authorization") String token);

    @POST("transactions/{transaction_id}/void")
    Call<ApiResponse> doVoid(@Path("transaction_id") String trxId,
                             @Header("Authorization") String token);

    @POST("transactions/void")
    Call<ApiResponse> doVoidArdi(@Body RequestBody requestBody,
                                 @Header("Authorization") String token);

    @POST("transactions/checkSettled")
    Call<ApiResponse> doCheckSettled(@Body RequestBody requestBody,
                                     @Header("Authorization") String token);


    @POST("transactions/settlement")
    Call<ApiResponse> doSettlement(@Body RequestBody requestBody,
                                   @Header("Authorization") String token);


    @GET
    Call<ApiResponse<List<com.boardinglabs.mireta.standalone.component.network.entities.Trx.Transactions>>> getHistoryWithFilter(@Url String url,
                                                                                                                                 @Query("location_id") String location_id,
                                                                                                                                 @Header("Authorization") String token);

    @GET
    Call<ApiResponse> getReportByFilter(@Url String url,
                                        @Query("location_id") String location_id,
                                        @Header("Authorization") String token);

    @POST("members")
    Call<ApiResponse> doRegisterMember(@Body RequestBody requestBody,
                                   @Header("Authorization") String token);

    @POST("auth/register")
    Call<ApiResponse> createUserArdi(@Body RequestBody requestBody);

    @POST("sk/activatePin")
    Call<ApiResponse> doActivatePin(@Body RequestBody requestBody,
                                       @Header("Authorization") String token);

    @GET("sk/securityQuestions")
    Call<ApiResponse<List<SecurityQuestions>>> getListQuestion(@Header("Authorization") String token);

    @POST("sk/validate")
    Call<ApiResponse> checkPIN(@Body RequestBody requestBody, @Header("Authorization") String token);

    @POST("sk/changePin")
    Call<ApiResponse> changePIN(@Body RequestBody requestBody, @Header("Authorization") String token);

    @POST("sk/resetPin")
    Call<ApiResponse> resetPIN(@Body RequestBody requestBody, @Header("Authorization") String token);

    @GET("members/searchCardNumber")
    Call<ApiResponse<CheckMemberResponse>> checkMember(@Query("card_number") String card_number,
                                                            @Header("Authorization") String token);

    @POST("members/exchangeFreeMeal")
    Call<ApiResponse> doCheckFreeMeal(@Body RequestBody requestBody,
                                       @Header("Authorization") String token);

    @FormUrlEncoded
    @POST("transactions/{id}/updateStatus")
    Call<ApiResponse<com.boardinglabs.mireta.standalone.component.network.entities.Trx.TransactionResponse>> updateTransaction(@Path("id") String id,
                                                                                                                               @Field("status") int status,
                                                                                                                               @Header("Authorization") String token);
}
