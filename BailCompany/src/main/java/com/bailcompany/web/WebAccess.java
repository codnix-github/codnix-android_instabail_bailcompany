package com.bailcompany.web;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.bailcompany.CompanyFilterActivity;
import com.bailcompany.GroupChatActivity;
import com.bailcompany.HistoryRequestList;
import com.bailcompany.HistoryRequestListNew;
import com.bailcompany.IndividualChatActivity;
import com.bailcompany.MainActivity;
import com.bailcompany.TrackAgents;
import com.bailcompany.model.AddOns;
import com.bailcompany.model.AgentBidingModel;
import com.bailcompany.model.AgentModel;
import com.bailcompany.model.BadDebtMember;
import com.bailcompany.model.BailRequestModel;
import com.bailcompany.model.BlackListMember;
import com.bailcompany.model.BondDocuments;
import com.bailcompany.model.ChatUser;
import com.bailcompany.model.CompanyBidingModel;
import com.bailcompany.model.CourtDateModel;
import com.bailcompany.model.FugitiveRequestModel;
import com.bailcompany.model.IndemnitorModel;
import com.bailcompany.model.InsuranceModel;
import com.bailcompany.model.MessageModel;
import com.bailcompany.model.PackageModel;
import com.bailcompany.model.Payment;
import com.bailcompany.model.StateModel;
import com.bailcompany.model.User;
import com.bailcompany.model.WarrantModel;
import com.bailcompany.ui.BadDebtMembers;
import com.bailcompany.ui.BlackListMembers;
import com.bailcompany.ui.Defendant;
import com.bailcompany.ui.IncomingBailRequest;
import com.bailcompany.ui.IncomingFugitveRequest;
import com.bailcompany.ui.IncomingRequest;
import com.bailcompany.ui.InstantChat;
import com.bailcompany.ui.InstantGroupChat;
import com.bailcompany.ui.SelfAssigned;
import com.bailcompany.utils.Commons;
import com.bailcompany.utils.Utils;
import com.dropbox.client2.session.Session.AccessType;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class WebAccess {
    // private static final String MAIN_URL =
    // "http://service.wadlinstabail.com/taxiwcf.svc/";
    // public static final String MAIN_URL =
    // "http://curio.webdesigninhoustontexas.com/instabail/services/";
    // public static final String MAIN_URL = "http://localhost/services/";
    public static final String MAIN_URL = "https://instabailapp.com/web/services/";
    // private static final String LOGIN_URL ="CompanyLogin";
    public static final String LOGIN_URL = "company-login";
    // private static final String LOGIN_FB_COMPANY_URL = "FBCompanyLogin";
    // private static final String REGISTER_URL = "RegisterCompany";
    public static final String REGISTER_URL = "register-company";
    // private static final String FORGOT_PASS = "ForgotCompanyPassword";
    // public static final String GET_ALL_AGENT = "GetAllAgent";
    public static final String GET_ALL_AGENT = "get-all-agents";
    public static final String CHECK_DEFENDANT_EXIST = "checkDefendantIsExist";
    public static final String GET_PAYMENT_INFO = "get-company-payment-method";
    public static final String GET_UPDATE_COMPANY_PAYMENT = "update-company-payment-method";
    final static public String ACCOUNT_PREFS_NAME = "prefs";
    final static public String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static public String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    public static final String MARK_REQUEST_READ = "mark-request-as-read";
    public static final String SEND_ARRIVED_TIME = "addUpdateAgentArrivedTime";

    // private static final String GET_ALL_INSURANCES = "GetAllInsurances";
    public static final String GET_ALL_INSURANCES = "get-all-insurances";
    public static final String GET_ALL_States = "get-all-states";
    public static final String GET_AN_AGENT = "get-an-agent";
    public static final String TRANSFER_BOND = "transfer-bond-request";
    public static final String REQUEST_FUGITIVE_AGENT = "get-a-fugitive-recovery-agent";
    public static final String ADD_BAD_DEBT_MEMBER = "add-bad-debit-members";
    public static final String ADD_BLACK_LIST_MEMBER = "add-black-list-members";
    public static final String REFER_BAIL = "reffer-a-bail-request";
    public static final String ACCEPT_BOND_REQUEST = "accept-bond-request";
    public static final String BAD_DEBT = "get-bad-debit-members";
    public static final String BLACKLIST = "get-black-list-members";
    public static final String REMOVE_BAD_DEBT = "remove-bad-debit-members";
    public static final String REMOVE_BLACK_LIST = "remove-black-list-members";
    public static final String GET_BID_ON_COMPANY = "bid-on-company-request";
    public static final String GET_COMPANY_DETAIL = "get-company-detail";
    public static final String GET_AGENT_DETAIL = "get-agent-detail";
    public static final String GET_UPDATE_MEMBER_PACKAGE = "Update-member-package";
    public static final String GET_ALL_BOND_REQUESTS = "get-all-bond-requests";
    public static final String GET_ALL_USER_CHAT = "get-user-chat-list";
    public static final String GET_SELF_REQUESTS = "get-self-assigned-bail-requests";
    public static final String GET_SENT_BOND_REQUESTS_HISTORY = "get-sent-bond-requests-history";
    public static final String GET_SENT_REFER_BAIL_REQUESTS_HISTORY = "get-sent-reffer-bail-requests";
    public static final String GET_ACCEPTED_BOND_REQUESTS_HISTORY = "get-accepted-bond-requests-history";
    public static final String GET_ACCEPTED_REFER_BAIL_REQUESTS_HISTORY = "get-accepted-reffer-bail-requests-history";
    public static final String GET_ALL_BAIL_REQUEST_HISTORY = "get-all-bail-requests";
    public static final String GET_ALL_BAIL_REQUESTS = "get-all-reffer-bail-requests";
    public static final String GET_ALL_MESSAGES = "get-message-history";
    public static final String GET_GROUP_CHAT_HISTORY = "get-group-chat-history";
    public static final String GET_SENT_FUGIITVE_AGENT = "get-sent-fugitive-recovery-agent-requests";
    public static final String GET_ACCEPT_FUGITVE_REQUEST = "accept-fugitive-recovery-agent-request";
    public static final String SELF_REQUEST = "send-request-to-self";
    public static final String SEND_REQUEST_AGENT = "send-request-to-agent";
    public static final ArrayList<String> defendantDetail = new ArrayList<String>();
    public static final String CONTACT_US = "contact-us";
    public static final String GET_AGENT_LOCATION = "get-agent-location";
    public static final String FORGOT_PASSWORD = "forgot-password";
    public static final String PHOTO = "http://web.instabailapp.com/";
    ;
    public static final String GET_REQUEST_DETAIL = "get-request-details";
    public static final String GET_DEFENDANT_LIST = "getAllDefendants";
    public static final String GET_DEFENDANT_DETAIL = "get-defendant-profile";
    public static final String UPDATE_DEFENDANT_ACCOUNT_STATUS = "updateDefendantAccountStatus";
    public static final String GET_DEFENDANT_BOND_DETAIL = "getDefendantBondDetails";
    public static final String GET_COMPANY_EVENTS = "get-events";
    public static final String ADD_COMPANY_EVENTS = "save-event";
    public static final String ADD_UPDATE_DEFENDANT_LOGIN_DETAILS = "addUpdateDefendantLoginDetails";


    public static final String UPDATE_BOND_DETAILS = "updateWarrant";
    public static final String UPLOAD_BOND_DOCUMENTS = "uploadBondDocuments";
    public static final String UPDATE_DEFENDENT_BASIC_PROFILE = "updateDefendantBasicProfile";
    public static final String ADD_UPDATE_DEFENDENT_EMPLOYMENT_DETAILS = "addDefendantEmploymentProfile";
    public static final String ADD_UPDATE_DEFENDENT_VEHICLE_DETAILS = "addDefendantVehicleDetails";
    public static final String ADD_UPDATE_DEFENDENT_NOTES_DETAILS = "addDefendantNote";


    public static final String GCM_SENDER_ID = "432754724650";
    public final static String Key_Push_Message = "message";
    public final static String Pkey_DEVICE_ID = "Device_ID";
    public static final String OVERRIDEMSG = "File name with this name already exists.Do you want to replace this file?";
    final static public String DROPBOX_APP_KEY = "80tofnlrsdv2a31";
    final static public String DROPBOX_APP_SECRET = "uyxq0n8wji67ucx";
    final static public AccessType ACCESS_TYPE = AccessType.DROPBOX;
    public static final String SUBMIT_COMPLETION_FORM = "submit-completion-form";
    public static final String SET_COMPLETE_STATUS = "set-complete-status";
    public static final String ABORT_REQUEST = "abort-request";
    public static RequestParams params = new RequestParams();
    public static boolean mLoggedIn = false;
    public static String agentRequestId;
    public static ArrayList<ArrayList<CompanyBidingModel>> AllBidListCompany = new ArrayList<ArrayList<CompanyBidingModel>>();
    public static ArrayList<ArrayList<AgentBidingModel>> AllBidList = new ArrayList<ArrayList<AgentBidingModel>>();
    public static boolean agentHire;
    public static boolean hireReferBailAgent;
    public static boolean hireBailAgent;
    public static boolean selfAssignAgent;
    public static boolean hireTransferBondAgent;
    public static boolean isIndividualChatOpen;
    public static boolean isPersonalGroupChatOpen;
    public static boolean isGroupChatOpen;
    public static String indUserId;
    public static String grpReqId;
    public static BailRequestModel agentRecord;
    public static boolean hireReq;
    public static boolean loginUser, fromFindMe, fromHistory;
    public static String type = null;
    public static boolean tranferBondBadge, referBailBadge, fugitiveBadge,
            instant, instantGroup;
    public static boolean selectedFile;
    static int getCallTimeout = 50000;
    static String response;
    static ProgressDialog pd;
    static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
    static Context context;
    private static User u;
    int i;
    String message;
    JSONObject jsonObj;
    String key;

    private static String executePostRequest(String restUrl,
                                             ArrayList<NameValuePair> param, boolean save) {

        if (param == null)
            param = new ArrayList<NameValuePair>();

        try {

            if (Utils.isOnline()) {
                JSONObject obj = new JSONObject();
                for (NameValuePair np : param) {
                    try {
                        if (np.getValue().startsWith("[")
                                && np.getValue().endsWith("]"))
                            obj.put(np.getName(), new JSONArray(np.getValue()));
                        else
                            obj.put(np.getName(), np.getValue());
                    } catch (Exception e) {
                        obj.put(np.getName(), np.getValue());
                    }

                }
                HttpPost post = new HttpPost(MAIN_URL + restUrl);
                // post.addHeader("Content-Type", "application/json");
                // post.setHeader("Accept", "application/json");
                StringEntity entity = new StringEntity(obj.toString(), "UTF-8");
                Log.e("Request", obj.toString());
                // entity.setContentType("application/json");
                post.setEntity(entity);
                HttpResponse res = new DefaultHttpClient().execute(post);
                if (res != null) {
                    String strRes = EntityUtils.toString(res.getEntity());
                    return strRes;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static User getCompanyResponse(String response) {
        User u;
        try {
            if (!Commons.isEmpty(response)) {
                Log.e("Output Response 1FB :", response);
                JSONObject json = new JSONObject(response);
                if (json.optString("status").equalsIgnoreCase("1")) {
                    JSONObject obj = json.getJSONObject("company_details");
                    ArrayList<String> insur = new ArrayList<String>();
                    ArrayList<String> insurName = new ArrayList<String>();
                    ArrayList<String> states = new ArrayList<String>();
                    ArrayList<String> statesName = new ArrayList<String>();
                    u = new User();

                    u.setCompanyName(obj.optString("CompanyName"));
                    u.setRolename(obj.optString("rolename"));
                    u.setName(obj.optString("ownername"));
                    u.setUsername(obj.optString("username"));
                    u.setCity(obj.optString("city"));
                    u.setState(obj.optString("state"));
                    u.setCountry(obj.optString("country"));
                    u.setLicenseExpi(obj.optString("licenseExpi"));
                    u.setLisencestate(obj.optString("lisencestate"));

                    u.setZipCode(obj.optString("ZIP"));
                    u.setAPT(obj.optString("APT"));
                    u.setBusinessPhone(obj.optString("BusinessPhone"));
                    u.setCompanyId(obj.optString("companyid"));
                    u.setEmail(obj.optString("email"));
                    u.setPassword(obj.optString("UserPassword"));
                    u.setAddress(obj.optString("address"));
                    u.setLicenseno(obj.optString("licenseno"));
                    u.setPhone(obj.optString("phone"));

                    u.setPhoto(PHOTO + obj.optString("photo"));
                    if (obj.getJSONArray("insurance").length() > 0) {
                        for (int j = 0; j < obj.getJSONArray("insurance")
                                .length(); j++) {
                            insur.add(obj.getJSONArray("insurance")
                                    .optString(j));
                        }
                        u.setInsurance(insur);
                    } else
                        u.setInsurance(null);

                    if (obj.getJSONArray("states").length() > 0) {
                        for (int j = 0; j < obj.getJSONArray("states").length(); j++) {
                            states.add(obj.getJSONArray("states").optString(j));
                        }
                        u.setStates(states);
                    } else
                        u.setStates(null);

                    if (obj.getJSONArray("insurancesName").length() > 0) {
                        for (int j = 0; j < obj.getJSONArray("insurancesName")
                                .length(); j++) {
                            insurName.add(obj.getJSONArray("insurancesName")
                                    .optString(j));
                        }
                        u.setInsurancesName(insurName);
                    } else
                        u.setInsurancesName(null);

                    if (obj.getJSONArray("statesName").length() > 0) {
                        for (int j = 0; j < obj.getJSONArray("statesName")
                                .length(); j++) {
                            statesName.add(obj.getJSONArray("statesName")
                                    .optString(j));
                        }
                        u.setStatesName(statesName);
                    } else
                        u.setStatesName(null);

                    return u;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<BailRequestModel> getAllDefendantBonds(String stRes) {
        ArrayList<BailRequestModel> bailReqList = new ArrayList<BailRequestModel>();
        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                Log.d("ResObj", resObj.toString());
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj
                                .getJSONArray("list_of_requests");


                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                BailRequestModel mod = parseDefendantRequestDetail(dObj);

                                if (mod != null) {
                                    Defendant.bailReqList.add(mod);
                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bailReqList;

    }

    public static ArrayList<BailRequestModel> getALLRequest(String stRes) {
        ArrayList<BailRequestModel> bailReqList = new ArrayList<BailRequestModel>();
        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                Log.d("ResObj", resObj.toString());
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj
                                .getJSONArray("list_of_requests");


                        if (dataArr != null && dataArr.length() > 0) {
                            int counter = 0;
                            CompanyFilterActivity.uniqueCompanyList.clear();
                            CompanyFilterActivity.selectedCompanyList.clear();
                            CompanyFilterActivity.previousItemStateArray.clear();
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                BailRequestModel mod = parseBailRequestDetail(dObj);

                                if (mod != null) {
                                    if (mod.getSenderCompanyName() != null && !CompanyFilterActivity.uniqueCompanyList.contains(mod.getSenderCompanyName())) {
                                        CompanyFilterActivity.uniqueCompanyList.add(mod.getSenderCompanyName());
                                        CompanyFilterActivity.previousItemStateArray.put(counter++, true);
                                        CompanyFilterActivity.selectedCompanyList.add(mod.getSenderCompanyName());
                                    }

                                    IncomingBailRequest.bailReqList.add(mod);
                                    IncomingRequest.bailReqList.add(mod);
                                    HistoryRequestList.bailReqList.add(mod);
                                    HistoryRequestListNew.bailReqList.add(mod);
                                    Defendant.bailReqList.add(mod);
                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bailReqList;

    }


    public static ArrayList<BailRequestModel> getDefendantBonds(String stRes) {
        ArrayList<BailRequestModel> bailReqList = new ArrayList<BailRequestModel>();
        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                Log.d("ResObj", resObj.toString());
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj
                                .getJSONArray("list_of_requests");


                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                BailRequestModel mod = parseDefendantRequestDetail(dObj);


                                if (mod != null) {
                                    Defendant.bailReqList.add(mod);
                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bailReqList;

    }

    public static ArrayList<ChatUser> getChatRequest(String stRes) {
        ArrayList<ChatUser> reqList = new ArrayList<ChatUser>();
        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj.getJSONArray("message");
                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                ChatUser mod = parseChatList(dObj);

                                if (mod != null) {
                                    InstantChat.msgListObj.add(mod);
                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return reqList;

    }

    public static ArrayList<BailRequestModel> getALLSelfRequest(String stRes) {
        ArrayList<BailRequestModel> bailReqList = new ArrayList<BailRequestModel>();
        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj
                                .getJSONArray("list_of_requests");
                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                BailRequestModel mod = parseSelfRequestDetail(dObj);

                                if (mod != null) {
                                    SelfAssigned.bailReqList.add(mod);

                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bailReqList;

    }

    public static ArrayList<BailRequestModel> getALLReferRequest(String stRes) {
        ArrayList<BailRequestModel> bailReqList = new ArrayList<BailRequestModel>();
        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj
                                .getJSONArray("list_of_requests");
                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                BailRequestModel mod = parseReferBailRequestDetail(dObj);

                                if (mod != null) {
                                    IncomingBailRequest.bailReqList.add(mod);
                                    HistoryRequestList.bailReqList.add(mod);
                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bailReqList;

    }

    public static void getALLIndividualMessages(String stRes) {

        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj.getJSONArray("message");
                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                MessageModel mod = parseIndividualMessage(dObj);

                                if (mod != null) {
                                    IndividualChatActivity.msgListObj.add(mod);

                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void getGroupMessages(String stRes) {

        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj.getJSONArray("chat_list");
                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                MessageModel mod = parseGroupMsg(dObj);

                                if (mod != null) {
                                    InstantGroupChat.msgListObj.add(mod);

                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void getALLGroupMessages(String stRes) {

        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj.getJSONArray("message");
                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                MessageModel mod = parseGroupMessage(dObj);
                                if (mod != null) {
                                    GroupChatActivity.msgListObj.add(mod);

                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Payment getPayment(String stRes) {
        Payment mod = null;
        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONObject jsonObj = resObj
                                .getJSONObject("payment_method");
                        if (jsonObj != null) {
                            mod = parsePaymentDetail(jsonObj);

                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mod;

    }

    public static ArrayList<FugitiveRequestModel> getALLFugitveRequest(
            String stRes) {
        ArrayList<FugitiveRequestModel> bailReqList = new ArrayList<FugitiveRequestModel>();
        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj
                                .getJSONArray("list_of_requests");
                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                FugitiveRequestModel mod = parseFugitiveRequestDetail(dObj);

                                if (mod != null)
                                    IncomingFugitveRequest.ReqList.add(mod);

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bailReqList;

    }

    public static ArrayList<BadDebtMember> getALLBadDebtRequest(String stRes) {
        ArrayList<BadDebtMember> badDebtList = new ArrayList<BadDebtMember>();
        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj
                                .getJSONArray("black_list_members");
                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                BadDebtMember mod = parseBadBedtRequestDetail(dObj);

                                if (mod != null)
                                    BadDebtMembers.badDebtList.add(mod);

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return badDebtList;

    }

    public static ArrayList<BlackListMember> getALLBlackListRequest(String stRes) {
        ArrayList<BlackListMember> badDebtList = new ArrayList<BlackListMember>();
        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj
                                .getJSONArray("black_list_members");
                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                BlackListMember mod = parseBlackListRequestDetail(dObj);

                                if (mod != null)
                                    BlackListMembers.blackList.add(mod);

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return badDebtList;

    }

    public static ArrayList<BailRequestModel> getALLRequestSender(String stRes) {
        ArrayList<BailRequestModel> bailReqList = new ArrayList<BailRequestModel>();
        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj
                                .getJSONArray("list_of_requests");
                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                BailRequestModel mod = parseBailRequestDetailReceiver(dObj);

                                if (mod != null)
                                    HistoryRequestList.bailReqList.add(mod);

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bailReqList;

    }

    public static ArrayList<BailRequestModel> getALLReferBailRequestSender(
            String stRes) {
        ArrayList<BailRequestModel> bailReqList = new ArrayList<BailRequestModel>();
        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj
                                .getJSONArray("list_of_requests");
                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                BailRequestModel mod = parseReferBailRequestDetailReceiver(dObj);

                                if (mod != null)
                                    HistoryRequestList.bailReqList.add(mod);

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bailReqList;

    }

    public static ArrayList<PackageModel> getALLPackages(String stRes) {
        ArrayList<PackageModel> packages = new ArrayList<PackageModel>();
        try {
            if (!Commons.isEmpty(stRes)) {
                JSONObject resObj = new JSONObject(stRes);
                if (resObj != null) {
                    if (resObj.optString("status").equalsIgnoreCase("1")) {
                        JSONArray dataArr = resObj.getJSONArray("packages");
                        if (dataArr != null && dataArr.length() > 0) {
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject dObj = dataArr.getJSONObject(i);
                                PackageModel mod = packageDetail(dObj);

                                if (mod != null) {
                                    if (!mod.getPackageType().equalsIgnoreCase(
                                            "2"))
                                        packages.add(mod);
                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return packages;

    }

    public static ArrayList<InsuranceModel> getAllInsurances(String response) {
        // String resp = executeArray(GET_ALL_INSURANCES, null);
        // Log.d("ins", resp + "");
        ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();

        try {
            if (!Commons.isEmpty(response)) {
                JSONObject json = new JSONObject(response);
                JSONArray arr = json.getJSONArray("list_of_insurances");
                if (json.optString("status").equalsIgnoreCase("1")
                        && arr != null && arr.length() > 0) {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        if (obj != null) {
                            InsuranceModel im = new InsuranceModel();
                            im.setId(obj.optInt("Id"));
                            im.setName(obj.optString("Name"));
                            im.setCreatedDate(obj.optString("CreatedDate"));
                            im.setCompanyName(obj.optString("CompanyName"));
                            im.setInsAddress(obj.optString("InsAddress"));
                            im.setAPT(obj.optString("APT"));
                            im.setTown(obj.optString("Town"));
                            im.setInsState(obj.optString("InsState"));
                            im.setZip(obj.optString("Zip"));
                            im.setBusinessPhone(obj.optString("BusinessPhone"));
                            im.setContatcPerson(obj.optString("ContatcPerson"));
                            im.setEmail(obj.optString("Email"));
                            im.setLicenseNo(obj.optString("LicenseNo"));
                            im.setForAgent(obj.optBoolean("ForAgent"));

                            insList.add(im);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return insList;
    }

    public static ArrayList<StateModel> getAllStates(String response) {
        // String resp = executeArray(GET_ALL_INSURANCES, null);
        // Log.d("ins", resp + "");
        ArrayList<StateModel> insList = new ArrayList<StateModel>();

        try {
            if (!Commons.isEmpty(response)) {
                JSONObject json = new JSONObject(response);
                JSONArray arr = json.getJSONArray("list_of_states");
                if (json.optString("status").equalsIgnoreCase("1")
                        && arr != null && arr.length() > 0) {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        if (obj != null) {
                            StateModel im = new StateModel();
                            im.setId(obj.optString("Id"));
                            im.setName(obj.optString("Name"));
                            im.setForAgent(obj.optString("ForAgent"));

                            insList.add(im);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return insList;
    }

    public static BailRequestModel parseDefendantRequestDetail(JSONObject dObj) {
        try {

            if (dObj != null) {
                BailRequestModel mod = new BailRequestModel();
                mod.setAgentRequestId(dObj.getInt("Id"));
                // mod.setId(dObj.getInt("Id"));
                mod.setDefendantName(dObj.getString("DefendantName"));
                mod.setDefDOB(dObj.getString("DefDOB"));
                mod.setDefSSN(dObj.getString("DefSSN"));

                mod.setSenderCompanyImage(PHOTO
                        + dObj.getString("CompanyImage"));
                mod.setSenderCompanyName(dObj.optString("CompanyName"));
                mod.setSenderCompanyId(dObj.optString("CompanyId"));
                mod.setSelfAssigned(dObj.optString("SelfAssigned") + "");
                mod.setPowerNo(dObj.optString("PowerNumber") + "");
                mod.setAmountReceived(dObj.optString("AmountReceived") + "");
                mod.setComments(dObj.optString("agent_comments") + "");

                mod.setIsAccept(dObj.getString("IsAccept"));
                mod.setIsComplete(dObj.getString("IsComplete"));
                mod.setIsAbort(dObj.getString("IsAborted"));
                mod.setIsCancel(dObj.getString("IsCancel"));

                mod.setRead(dObj.getString("Read"));
                mod.setDefBookingNumber(dObj.getString("DefBookingNumber"));
                mod.setLocationLongitude(dObj.getString("LocationLongitude"));
                mod.setLocationLatitude(dObj.getString("LocationLatitude"));

                mod.setLocation(dObj.getString("Location"));
                mod.setPaymentPlan(dObj.getString("PaymentPlan"));
                mod.setCreatedDate(dObj.getString("CreatedDate"));
                mod.setInstructionForAgent(dObj
                        .getString("InstructionForAgent"));
                mod.setNumberIndemnitors(dObj.getString("NumberIndemnitors"));
                mod.setAmountToCollect(dObj.getString("AmountToCollect"));
                mod.setAgentImage(PHOTO + dObj.getString("AgentImage"));
                mod.setAgentName(dObj.getString("Name"));
                mod.setAgentId(dObj.getString("AgentId"));
                String temp;
                temp = dObj.getString("NeedCourtFee");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedCourtFee(true);
                } else {
                    mod.setNeedCourtFee(false);
                }
                temp = dObj.getString("NeedBailSource");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedBailSource(true);
                } else {
                    mod.setNeedBailSource(false);
                }
                temp = dObj.getString("IsCallAgency");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setIsCallAgency(true);
                } else {
                    mod.setIsCallAgency(false);
                }
                temp = dObj.getString("NeedPaperWork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedPaperWork(true);
                } else {
                    mod.setNeedPaperWork(false);
                }
                temp = dObj.getString("NeedIndemnitorPaperwork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedIndemnitorPaperwork(true);
                } else {
                    mod.setNeedIndemnitorPaperwork(false);
                }
                temp = dObj.getString("NeedDefendantPaperwork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedDefendantPaperwork(true);
                } else {
                    mod.setNeedDefendantPaperwork(false);
                }
                temp = dObj.getString("PaymentAlreadyReceived");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setPaymentAlreadyReceived(true);
                } else {
                    mod.setPaymentAlreadyReceived(false);
                }

                mod.setSentRequestTime(dObj.getString("SentRequestTime"));//

                JSONArray wArr = dObj.getJSONArray("WarrantList");
                ArrayList<WarrantModel> wList = new ArrayList<WarrantModel>();
                if (wArr != null && wArr.length() > 0) {
                    for (int i = 0; i < wArr.length(); i++) {
                        JSONObject wObj = wArr.getJSONObject(i);
                        if (wObj != null) {
                            WarrantModel wMod = new WarrantModel();
                            wMod.setId(wObj.optInt("Id"));
                            wMod.setAmount(wObj.optString("Amount"));
                            wMod.setTownship(wObj.optString("Township"));
                            wMod.setCourtDate(wObj.optString("CourtDate"));
                            wMod.setNotes(wObj.optString("Notes"));
                            wMod.setStatus(wObj.optString("Status"));

                            if (wObj.has("case_no") && wObj.optString("case_no") != null && !wObj.optString("case_no").equals(""))
                                wMod.setCase_no(wObj.optString("case_no"));
                            if (wObj.has("PowerNo") && wObj.optString("PowerNo") != null && !wObj.optString("PowerNo").equals(""))
                                wMod.setPowerNo(wObj.optString("PowerNo"));
                            if (wObj.has("Notes") && wObj.optString("Notes") != null && !wObj.optString("Notes").equals(""))
                                wMod.setNotes(wObj.optString("Notes"));
                            else
                                wMod.setNotes(wObj.optString(""));


                            wList.add(wMod);
                        }
                    }
                }
                mod.setWarrantList(wList);

                JSONArray wCourtDates = dObj.getJSONArray("CourtDates");
                ArrayList<CourtDateModel> cDatesList = new ArrayList<CourtDateModel>();
                if (wCourtDates != null && wCourtDates.length() > 0) {
                    for (int i = 0; i < wCourtDates.length(); i++) {
                        JSONArray arrCourdt = wCourtDates.getJSONArray(i);
                        for (int j = 0; j < arrCourdt.length(); j++) {
                            JSONObject iObj = arrCourdt.getJSONObject(j);
                            if (iObj != null) {
                                CourtDateModel oCDate = new CourtDateModel();
                                oCDate.setId(iObj.getInt("Id"));
                                oCDate.setWarrantId(iObj.getInt("WarrentId"));
                                oCDate.setCourtDate(iObj.optString("CourtDate"));
                                cDatesList.add(oCDate);
                            }
                        }

                    }
                }
                mod.setCourtDates(cDatesList);

                JSONObject objDocuments = dObj.getJSONObject("documents");

                BondDocuments bondDocumentList = new BondDocuments();

                if (objDocuments != null) {

                    JSONArray docCosigner = objDocuments.getJSONArray("cosigner");


                    ArrayList<String> tempDoc = new ArrayList<>();
                    for (int i = 0; i < docCosigner.length(); i++) {
                        tempDoc.add(docCosigner.get(i).toString());
                    }
                    bondDocumentList.setCosignerPhoto(tempDoc);
                    JSONArray docDefendant = objDocuments.getJSONArray("defendent");

                    ArrayList<String> tempDoc1 = new ArrayList<String>();
                    for (int i = 0; i < docDefendant.length(); i++) {
                        tempDoc1.add(docDefendant.get(i).toString());
                    }
                    bondDocumentList.setDefendantPhoto(tempDoc1);
                    JSONArray docAttachment = objDocuments.getJSONArray("attachments");

                    ArrayList<String> tempDoc2 = new ArrayList<String>();
                    for (int i = 0; i < docAttachment.length(); i++) {
                        tempDoc2.add(docAttachment.get(i).toString());
                    }
                    bondDocumentList.setOtherDocuments(tempDoc2);


                }
                mod.setBondDocuments(bondDocumentList);

                Log.d("BCosigner=", "" + mod.getBondDocuments().getCosignerPhoto().size());
                Log.d("BDef=", "" + mod.getBondDocuments().getDefendantPhoto().size());
                Log.d("BOther=", "" + mod.getBondDocuments().getOtherDocuments().size());

                JSONArray iArr = dObj.getJSONArray("IndemnitorsList");
                ArrayList<IndemnitorModel> iList = new ArrayList<IndemnitorModel>();
                if (iArr != null && iArr.length() > 0) {
                    for (int i = 0; i < iArr.length(); i++) {
                        JSONObject iObj = iArr.getJSONObject(i);
                        if (iObj != null) {
                            IndemnitorModel iMod = new IndemnitorModel();
                            iMod.setName(iObj.optString("Name"));
                            iMod.setFName(iObj.optString("ind_firstname"));
                            iMod.setLName(iObj.optString("ind_lastname"));
                            iMod.setPhoneNumber(iObj.optString("PhoneNumber"));

                            iList.add(iMod);
                        }
                    }
                }
                mod.setIndemnitorsList(iList);

                JSONArray insArr = dObj.getJSONArray("InsuranceList");
                ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();
                if (insArr != null && insArr.length() > 0) {
                    for (int i = 0; i < insArr.length(); i++) {
                        JSONObject insObj = insArr.getJSONObject(i);
                        if (insObj != null) {
                            InsuranceModel insMod = new InsuranceModel();
                            insMod.setId(Integer.parseInt(insObj
                                    .optString("Id")));
                            insMod.setName(insObj.optString("Name"));

                            insList.add(insMod);
                        }
                    }
                }
                mod.setInsuranceList(insList);

                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static BailRequestModel parseBailRequestDetail(JSONObject dObj) {
        try {

            if (dObj != null) {
                BailRequestModel mod = new BailRequestModel();
                mod.setAgentRequestId(dObj.getInt("Id"));
                // mod.setId(dObj.getInt("Id"));
                mod.setDefendantName(dObj.getString("DefendantName"));
                mod.setDefendantFName(dObj.optString("firstname"));
                mod.setDefendantLName(dObj.optString("lastname"));
                mod.setDefDOB(dObj.getString("DefDOB"));
                mod.setDefSSN(dObj.getString("DefSSN"));

                if (dObj.has("DefId"))
                    mod.setDefId(dObj.getString("DefId"));
                else
                    mod.setDefId(dObj.getString(""));

                if (HistoryRequestList.IsBailRequest
                        || TrackAgents.IsBailRequest) {
                    mod.setSenderCompanyImage(PHOTO
                            + dObj.getString("CompanyImage"));
                    mod.setSenderCompanyName(dObj.optString("CompanyName"));
                    mod.setSenderCompanyId(dObj.optString("CompanyId"));
                    mod.setSelfAssigned(dObj.optString("SelfAssigned") + "");
                    mod.setPowerNo(dObj.optString("PowerNumber") + "");
                    mod.setAmountReceived(dObj.optString("AmountReceived") + "");
                    mod.setComments(dObj.optString("agent_comments") + "");

                } else {
                    mod.setCompanyOfferToPay(dObj
                            .getString("CompanyOfferToPay"));
                    mod.setSenderCompanyImage(PHOTO
                            + dObj.getString("SenderCompanyImage"));
                    mod.setSenderCompanyName(dObj
                            .optString("SenderCompanyName"));
                    mod.setSenderCompanyId(dObj.optString("SenderCompanyId"));
                    mod.setBondRequestCompany(dObj
                            .getString("BondRequestCompany"));
                    mod.setBondInsuranceId(dObj.getString("BondInsuranceId"));
                    mod.setBondInsuranceName(dObj
                            .getString("BondInsuranceName"));
                    ArrayList<CompanyBidingModel> iList2 = new ArrayList<CompanyBidingModel>();
                    JSONArray iArr3 = dObj.getJSONArray("YourBid");
                    if (iArr3 != null && iArr3.length() > 0) {
                        JSONObject iObj = iArr3.getJSONObject(0);
                        if (iObj != null) {
                            CompanyBidingModel iMod = new CompanyBidingModel();
                            iMod.setCompanyId(iObj.optString("CompanyId"));
                            iMod.setAmount(iObj.optString("Amount"));
                            iMod.setCompanyName(iObj.optString("CompanyName"));
                            iMod.setCompanyImage(PHOTO
                                    + iObj.optString("CompanyImage"));
                            iMod.setDescription(iObj.optString("Description"));
                            iMod.setAcceptedStatus(iObj
                                    .optString("AcceptedStatus"));
                            iMod.setRequestId(iObj.optString("RequestId"));
                            iMod.setBidingId(iObj.optString("bidding_id"));

                            iList2.add(iMod);
                        } else {
                            iList2.add(null);
                        }

                    }
                    AllBidListCompany.add(iList2);
                }
                if (!IncomingRequest.incomTranferBond) {

                    mod.setIsAccept(dObj.getString("IsAccept"));
                    mod.setIsComplete(dObj.getString("IsComplete"));
                    mod.setIsAbort(dObj.getString("IsAborted"));
                    mod.setIsCancel(dObj.getString("IsCancel"));
                }
                mod.setRead(dObj.getString("Read"));
                mod.setDefBookingNumber(dObj.getString("DefBookingNumber"));
                mod.setLocationLongitude(dObj.getString("LocationLongitude"));
                mod.setLocationLatitude(dObj.getString("LocationLatitude"));

                mod.setLocation(dObj.getString("Location"));
                mod.setPaymentPlan(dObj.getString("PaymentPlan"));
                mod.setCreatedDate(dObj.getString("CreatedDate"));
                mod.setInstructionForAgent(dObj
                        .getString("InstructionForAgent"));
                mod.setNumberIndemnitors(dObj.getString("NumberIndemnitors"));
                mod.setAmountToCollect(dObj.getString("AmountToCollect"));
                mod.setAgentImage(PHOTO + dObj.getString("AgentImage"));
                mod.setAgentName(dObj.getString("Name"));
                mod.setAgentId(dObj.getString("AgentId"));
                String temp;
                temp = dObj.getString("NeedCourtFee");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedCourtFee(true);
                } else {
                    mod.setNeedCourtFee(false);
                }
                temp = dObj.getString("NeedBailSource");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedBailSource(true);
                } else {
                    mod.setNeedBailSource(false);
                }
                temp = dObj.getString("IsCallAgency");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setIsCallAgency(true);
                } else {
                    mod.setIsCallAgency(false);
                }
                temp = dObj.getString("NeedPaperWork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedPaperWork(true);
                } else {
                    mod.setNeedPaperWork(false);
                }
                temp = dObj.getString("NeedIndemnitorPaperwork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedIndemnitorPaperwork(true);
                } else {
                    mod.setNeedIndemnitorPaperwork(false);
                }
                temp = dObj.getString("NeedDefendantPaperwork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedDefendantPaperwork(true);
                } else {
                    mod.setNeedDefendantPaperwork(false);
                }
                temp = dObj.getString("PaymentAlreadyReceived");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setPaymentAlreadyReceived(true);
                } else {
                    mod.setPaymentAlreadyReceived(false);
                }

                mod.setSentRequestTime(dObj.getString("SentRequestTime"));//


                mod.setAgentAcceptedTime(dObj.getString("AgentAcceptedTime"));//
                mod.setAgentArrivedTime(dObj.getString("AgentArrivedTime"));//
                mod.setRequestCompletionTime(dObj.getString("RequestCompletionTime"));//
                mod.setRequestAbortedTime(dObj.getString("RequestAbortedTime"));//


                JSONArray wArr = dObj.getJSONArray("WarrantList");
                ArrayList<WarrantModel> wList = new ArrayList<WarrantModel>();
                if (wArr != null && wArr.length() > 0) {
                    for (int i = 0; i < wArr.length(); i++) {
                        JSONObject wObj = wArr.getJSONObject(i);
                        if (wObj != null) {
                            WarrantModel wMod = new WarrantModel();
                            wMod.setAmount(wObj.optString("Amount"));
                            wMod.setTownship(wObj.optString("Township"));
                            wMod.setAmountReceived(wObj.optString("WarrantAmountRecieved"));


                            if (wObj.has("case_no") && wObj.optString("case_no") != null && !wObj.optString("case_no").equals(""))
                                wMod.setCase_no(wObj.optString("case_no"));
                            if (wObj.has("PowerNo") && wObj.optString("PowerNo") != null && !wObj.optString("PowerNo").equals(""))
                                wMod.setPowerNo(wObj.optString("PowerNo"));

                            wList.add(wMod);
                        }
                    }
                }
                mod.setWarrantList(wList);

                JSONArray iArr = dObj.getJSONArray("IndemnitorsList");
                ArrayList<IndemnitorModel> iList = new ArrayList<IndemnitorModel>();
                if (iArr != null && iArr.length() > 0) {
                    for (int i = 0; i < iArr.length(); i++) {
                        JSONObject iObj = iArr.getJSONObject(i);
                        if (iObj != null) {
                            IndemnitorModel iMod = new IndemnitorModel();
                            iMod.setName(iObj.optString("Name"));
                            iMod.setFName(iObj.optString("ind_firstname"));
                            iMod.setLName(iObj.optString("ind_lastname"));
                            iMod.setPhoneNumber(iObj.optString("PhoneNumber"));

                            iList.add(iMod);
                        }
                    }
                }
                mod.setIndemnitorsList(iList);

                JSONArray insArr = dObj.getJSONArray("InsuranceList");
                ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();
                if (insArr != null && insArr.length() > 0) {
                    for (int i = 0; i < insArr.length(); i++) {
                        JSONObject insObj = insArr.getJSONObject(i);
                        if (insObj != null) {
                            InsuranceModel insMod = new InsuranceModel();
                            insMod.setId(Integer.parseInt(insObj
                                    .optString("Id")));
                            insMod.setName(insObj.optString("Name"));

                            insList.add(insMod);
                        }
                    }
                }
                mod.setInsuranceList(insList);

                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static BailRequestModel parseSelfRequestDetail(JSONObject dObj) {
        try {

            if (dObj != null) {
                BailRequestModel mod = new BailRequestModel();
                mod.setAgentRequestId(dObj.getInt("Id"));

                mod.setDefendantName(dObj.getString("DefendantName"));
                mod.setDefDOB(dObj.getString("DefDOB"));
                mod.setDefSSN(dObj.getString("DefSSN"));

                mod.setSenderCompanyImage(PHOTO
                        + dObj.getString("CompanyImage"));
                mod.setSenderCompanyName(dObj.optString("CompanyName"));
                mod.setSenderCompanyId(dObj.optString("CompanyId"));
                mod.setSelfAssigned(dObj.optString("SelfAssigned") + "");

                mod.setIsAccept(dObj.getString("IsAccept"));
                mod.setIsComplete(dObj.getString("IsComplete"));
                mod.setIsAbort(dObj.getString("IsAborted"));
                mod.setIsCancel(dObj.getString("IsCancel"));
                mod.setRead(dObj.getString("Read"));
                mod.setDefBookingNumber(dObj.getString("DefBookingNumber"));
                mod.setLocationLongitude(dObj.getString("LocationLongitude"));
                mod.setLocationLatitude(dObj.getString("LocationLatitude"));

                mod.setLocation(dObj.getString("Location"));
                mod.setPaymentPlan(dObj.getString("PaymentPlan"));
                mod.setCreatedDate(dObj.getString("CreatedDate"));
                mod.setInstructionForAgent(dObj
                        .getString("InstructionForAgent"));
                mod.setNumberIndemnitors(dObj.getString("NumberIndemnitors"));
                mod.setAmountToCollect(dObj.getString("AmountToCollect"));
                mod.setAgentImage(PHOTO + dObj.getString("AgentImage"));
                mod.setAgentName(dObj.getString("Name"));
                mod.setAgentId(dObj.getString("AgentId"));
                String temp;
                temp = dObj.getString("NeedCourtFee");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedCourtFee(true);
                } else {
                    mod.setNeedCourtFee(false);
                }
                temp = dObj.getString("NeedBailSource");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedBailSource(true);
                } else {
                    mod.setNeedBailSource(false);
                }
                temp = dObj.getString("IsCallAgency");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setIsCallAgency(true);
                } else {
                    mod.setIsCallAgency(false);
                }
                temp = dObj.getString("NeedPaperWork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedPaperWork(true);
                } else {
                    mod.setNeedPaperWork(false);
                }
                temp = dObj.getString("NeedIndemnitorPaperwork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedIndemnitorPaperwork(true);
                } else {
                    mod.setNeedIndemnitorPaperwork(false);
                }
                temp = dObj.getString("NeedDefendantPaperwork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedDefendantPaperwork(true);
                } else {
                    mod.setNeedDefendantPaperwork(false);
                }
                temp = dObj.getString("PaymentAlreadyReceived");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setPaymentAlreadyReceived(true);
                } else {
                    mod.setPaymentAlreadyReceived(false);
                }

                mod.setSentRequestTime(dObj.getString("SentRequestTime"));//

                JSONArray wArr = dObj.getJSONArray("WarrantList");
                ArrayList<WarrantModel> wList = new ArrayList<WarrantModel>();
                if (wArr != null && wArr.length() > 0) {
                    for (int i = 0; i < wArr.length(); i++) {
                        JSONObject wObj = wArr.getJSONObject(i);
                        if (wObj != null) {
                            WarrantModel wMod = new WarrantModel();
                            wMod.setAmount(wObj.optString("Amount"));
                            wMod.setTownship(wObj.optString("Township"));

                            wList.add(wMod);
                        }
                    }
                }
                mod.setWarrantList(wList);

                JSONArray iArr = dObj.getJSONArray("IndemnitorsList");
                ArrayList<IndemnitorModel> iList = new ArrayList<IndemnitorModel>();
                if (iArr != null && iArr.length() > 0) {
                    for (int i = 0; i < iArr.length(); i++) {
                        JSONObject iObj = iArr.getJSONObject(i);
                        if (iObj != null) {
                            IndemnitorModel iMod = new IndemnitorModel();
                            iMod.setName(iObj.optString("Name"));
                            iMod.setFName(iObj.optString("ind_firstname"));
                            iMod.setLName(iObj.optString("ind_lastname"));
                            iMod.setPhoneNumber(iObj.optString("PhoneNumber"));

                            iList.add(iMod);
                        }
                    }
                }
                mod.setIndemnitorsList(iList);

                JSONArray insArr = dObj.getJSONArray("InsuranceList");
                ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();
                if (insArr != null && insArr.length() > 0) {
                    for (int i = 0; i < insArr.length(); i++) {
                        JSONObject insObj = insArr.getJSONObject(i);
                        if (insObj != null) {
                            InsuranceModel insMod = new InsuranceModel();
                            insMod.setId(Integer.parseInt(insObj
                                    .optString("Id")));
                            insMod.setName(insObj.optString("Name"));

                            insList.add(insMod);
                        }
                    }
                }
                mod.setInsuranceList(insList);

                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static BailRequestModel parseReferBailRequestDetail(JSONObject dObj) {
        try {

            if (dObj != null) {
                BailRequestModel mod = new BailRequestModel();
                mod.setAgentRequestId(dObj.getInt("Id"));
                // mod.setId(dObj.getInt("Id"));
                mod.setDefendantName(dObj.getString("DefendantName"));
                mod.setDefendantFName(dObj.optString("firstname"));
                mod.setDefendantLName(dObj.optString("lastname"));
                mod.setDefDOB(dObj.getString("DefDOB"));

                mod.setSenderCompanyImage(PHOTO
                        + dObj.getString("SenderCompanyImage"));
                mod.setSenderCompanyName(dObj.optString("SenderCompanyName"));
                mod.setSenderCompanyId(dObj.optString("SenderCompanyId"));
                mod.setBondRequestCompany(dObj.getString("BondRequestCompany"));

                mod.setLocationLongitude(dObj.getString("LocationLongitude"));
                mod.setLocationLatitude(dObj.getString("LocationLatitude"));
                mod.setRead(dObj.getString("Read"));
                mod.setLocation(dObj.getString("Location"));
                mod.setPaymentPlan(dObj.getString("PaymentPlan"));
                mod.setCreatedDate(dObj.getString("CreatedDate"));
                mod.setInstructionForAgent(dObj
                        .getString("InstructionForAgent"));
                mod.setNumberIndemnitors(dObj.getString("NumberofCosigner"));
                mod.setAmountToCollect(dObj.getString("AmountToCollect"));
                mod.setAmmountForCommission(dObj
                        .getString("AmountForCommission"));
                mod.setAgentId(dObj.getString("AgentId"));
                String temp;

                temp = dObj.getString("PaymentAlreadyReceived");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setPaymentAlreadyReceived(true);
                } else {
                    mod.setPaymentAlreadyReceived(false);
                }

                mod.setSentRequestTime(dObj.getString("SentRequestTime"));//

                JSONArray wArr = dObj.getJSONArray("WarrantList");
                ArrayList<WarrantModel> wList = new ArrayList<WarrantModel>();
                if (wArr != null && wArr.length() > 0) {
                    for (int i = 0; i < wArr.length(); i++) {
                        JSONObject wObj = wArr.getJSONObject(i);
                        if (wObj != null) {
                            WarrantModel wMod = new WarrantModel();
                            wMod.setAmount(wObj.optString("Amount"));
                            wMod.setTownship(wObj.optString("Township"));

                            wList.add(wMod);
                        }
                    }
                }
                mod.setWarrantList(wList);

                JSONArray insArr = dObj.getJSONArray("InsuranceList");
                ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();
                if (insArr != null && insArr.length() > 0) {
                    for (int i = 0; i < insArr.length(); i++) {
                        JSONObject insObj = insArr.getJSONObject(i);
                        if (insObj != null) {
                            InsuranceModel insMod = new InsuranceModel();
                            insMod.setId(Integer.parseInt(insObj
                                    .optString("Id")));
                            insMod.setName(insObj.optString("Name"));

                            insList.add(insMod);
                        }
                    }
                }
                mod.setInsuranceList(insList);
                ArrayList<CompanyBidingModel> iList2 = new ArrayList<CompanyBidingModel>();
                JSONArray iArr3 = dObj.getJSONArray("YourBid");
                if (iArr3 != null && iArr3.length() > 0) {
                    JSONObject iObj = iArr3.getJSONObject(0);
                    if (iObj != null) {
                        CompanyBidingModel iMod = new CompanyBidingModel();
                        iMod.setCompanyId(iObj.optString("CompanyId"));
                        iMod.setAmount(iObj.optString("Amount"));
                        iMod.setCompanyName(iObj.optString("CompanyName"));
                        iMod.setCompanyImage(PHOTO
                                + iObj.optString("CompanyImage"));
                        iMod.setDescription(iObj.optString("Description"));
                        iMod.setAcceptedStatus(iObj.optString("AcceptedStatus"));
                        iMod.setRequestId(iObj.optString("RequestId"));
                        iMod.setBidingId(iObj.optString("bidding_id"));

                        iList2.add(iMod);
                    } else {
                        iList2.add(null);
                    }

                }
                AllBidListCompany.add(iList2);
                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static FugitiveRequestModel parseFugitiveRequestDetail(
            JSONObject dObj) {
        try {
            if (dObj != null) {
                FugitiveRequestModel mod = new FugitiveRequestModel();
                mod.setId(dObj.getString("Id"));

                mod.setDefendantName(dObj.getString("DefendantName"));
                mod.setDefendantAddress(dObj.getString("Location"));
                mod.setDefDOB(dObj.getString("DefDOB"));
                mod.setDefSSN(dObj.getString("DefSSN"));
                mod.setDefBookingNumber(dObj.getString("DefBookingNumber"));
                mod.setDefendantImage(PHOTO + dObj.getString("DefendentPhoto"));
                mod.setLocationLongitude(dObj.getString("LocationLongitude"));
                mod.setLocationLatitude(dObj.getString("LocationLatitude"));
                mod.setHomeNumber(dObj.getString("HomeNumber"));
                mod.setCellNumber(dObj.getString("CellNumber"));
                // mod.setForefeiture(dObj.getString("ForfeitureAmount"));
                mod.setIsFemale(dObj.getString("IsFemale"));
                // mod.setTownShip(dObj.getString("Township"));
                mod.setLocation(dObj.getString("Location"));
                mod.setDayLeftBeforeBailSeized(dObj
                        .getString("DayLeftBeforeBailSeized"));
                // String str = dObj.getString("IsAccept") + "";
                // if (str.equalsIgnoreCase("1"))
                // mod.setIsAccept(true);
                // else
                // mod.setIsAccept(false);
                mod.setRead(dObj.getString("Read"));
                mod.setIsAccept(dObj.getString("IsAccept"));
                mod.setIsComplete(dObj.getString("IsComplete"));
                mod.setIsAbort(dObj.getString("IsAborted"));
                mod.setIsCancel(dObj.getString("IsCancel"));
                mod.setRecoveryStateID(dObj.getString("RecoveryStateID"));
                mod.setRecoveryStateName(dObj.getString("RecoveryStateName"));

                mod.setCreatedDate(dObj.getString("CreatedDate"));
                mod.setInstructionForAgent(dObj
                        .getString("InstructionForAgent"));

                mod.setAmountorPercentage(dObj.getString("AmountorPercentage"));

                mod.setAgentId(dObj.getString("AgentId"));
                mod.setAgentName(dObj.getString("Name"));
                mod.setAgentImage(PHOTO + dObj.getString("AgentImage"));
                String temp;
                temp = dObj.getString("NeedCourtFee");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedCourtFee(true);
                } else {
                    mod.setNeedCourtFee(false);
                }
                temp = dObj.getString("NeedBailSource");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedBailSource(true);
                } else {
                    mod.setNeedBailSource(false);
                }
                temp = dObj.getString("IsCallAgency");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setIsCallAgency(true);
                } else {
                    mod.setIsCallAgency(false);
                }
                temp = dObj.getString("NeedPaperWork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedPaperWork(true);
                } else {
                    mod.setNeedPaperWork(false);
                }
                temp = dObj.getString("NeedIndemnitorPaperwork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedIndemnitorPaperwork(true);
                } else {
                    mod.setNeedIndemnitorPaperwork(false);
                }
                temp = dObj.getString("NeedDefendantPaperwork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedDefendantPaperwork(true);
                } else {
                    mod.setNeedDefendantPaperwork(false);
                }
                JSONArray wArr = dObj.getJSONArray("WarrantList");
                ArrayList<WarrantModel> wList = new ArrayList<WarrantModel>();
                if (wArr != null && wArr.length() > 0) {
                    for (int i = 0; i < wArr.length(); i++) {
                        JSONObject wObj = wArr.getJSONObject(i);
                        if (wObj != null) {
                            WarrantModel wMod = new WarrantModel();
                            wMod.setAmount(wObj.optString("Amount"));
                            wMod.setTownship(wObj.optString("Township"));

                            wList.add(wMod);
                        }
                    }
                }
                mod.setWarrantList(wList);
                mod.setAmountCompanyWillToPay(dObj
                        .getString("AmountCompanyWillToPay"));

                mod.setSentRequestTime(dObj.getString("SentRequestTime"));//

                JSONArray iArr2 = dObj.getJSONArray("AgentsBiddings");
                ArrayList<AgentBidingModel> iList2 = new ArrayList<AgentBidingModel>();
                if (iArr2 != null && iArr2.length() > 0) {
                    for (int i = 0; i < iArr2.length(); i++) {
                        JSONObject iObj = iArr2.getJSONObject(i);
                        if (iObj != null) {
                            AgentBidingModel iMod = new AgentBidingModel();
                            iMod.setAgentId(iObj.optString("AgentId"));
                            iMod.setAmount(iObj.optString("Amount"));
                            iMod.setAgentName(iObj.optString("AgentName"));
                            iMod.setAgentImage(PHOTO
                                    + iObj.optString("AgentImage"));
                            iMod.setDescription(iObj.optString("Description"));
                            iMod.setAcceptedStatus(iObj
                                    .optString("AcceptedStatus"));
                            iMod.setRequestId(iObj.optString("RequestId"));

                            iList2.add(iMod);
                        }
                    }
                }
                AllBidList.add(iList2);

                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static MessageModel parseIndividualMessage(JSONObject dObj) {
        try {
            if (dObj != null) {
                MessageModel mod = new MessageModel();
                mod.setMessageRead(dObj.optString("MessageRead"));
                mod.setMessageId(dObj.optString("MessageId"));
                mod.setFromUserId(dObj.optString("MessageFrom"));
                mod.setToUserId(dObj.optString("MessageTo"));
                mod.setMessage(dObj.optString("MessageText"));

                mod.setTime(dObj.optString("MessageTime"));

                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static MessageModel parseGroupMsg(JSONObject dObj) {
        try {
            if (dObj != null) {
                MessageModel mod = new MessageModel();
                mod.setMessageId(dObj.optString("MessageId"));
                mod.setFromUserId(dObj.optString("MessageFrom"));
                mod.setMessage(dObj.optString("MessageText"));
                mod.setFromName(dObj.optString("Name"));
                mod.setFromPhoto(dObj.optString("Photo"));
                mod.setTime(dObj.optString("MessageTime"));

                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ChatUser parseChatList(JSONObject dObj) {
        try {
            if (dObj != null) {
                ChatUser mod = new ChatUser();

                mod.setMessageFrom(dObj.getString("MessageFrom"));
                mod.setMessageTo(dObj.getString("MessageTo"));
                mod.setRoleIdB(dObj.getString("RoleIdB"));
                mod.setUserRole(dObj.getString("UserRole"));
                mod.setUserName(dObj.getString("UserName"));
                mod.setUserPhoto(dObj.getString("UserPhoto"));
                mod.setSocketId(dObj.getString("SocketId"));
                JSONObject dObj2 = (dObj.getJSONObject("LastMessage"));
                MessageModel mod2 = parseIndividualMessage(dObj2);
                mod.setLastMessage(mod2);
                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static MessageModel parseGroupMessage(JSONObject dObj) {
        try {
            if (dObj != null) {
                MessageModel mod = new MessageModel();
                mod.setMessageId(dObj.getString("MessageId"));
                mod.setFromUserId(dObj.getString("MessageFrom"));
                mod.setRequestid(dObj.getString("RequestId"));
                mod.setMessage(dObj.getString("MessageText"));
                mod.setTime(dObj.getString("MessageTime"));
                mod.setFromRole(dObj.getString("Role"));
                mod.setFromName(dObj.getString("Name"));
                mod.setFromPhoto(dObj.getString("Photo"));
                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static BailRequestModel parseBailRequestDetailReceiver(
            JSONObject dObj) {
        try {

            if (dObj != null) {
                BailRequestModel mod = new BailRequestModel();
                mod.setAgentRequestId(dObj.getInt("Id"));
                // mod.setId(dObj.getInt("Id"));
                mod.setDefendantName(dObj.getString("DefendantName"));
                mod.setDefDOB(dObj.getString("DefDOB"));
                mod.setDefSSN(dObj.getString("DefSSN"));

                if (HistoryRequestList.title
                        .equalsIgnoreCase("My Sent Transfer Bond Requests")) {
                    // mod.setSenderCompanyImage(PHOTO
                    // + dObj.getString("SenderCompanyImage"));
                    // mod.setSenderCompanyName(dObj
                    // .getString("SenderCompanyName"));
                    // mod.setSenderCompanyId(dObj.getString("SenderCompanyId"));
                    mod.setBondRequestCompany(dObj
                            .getString("BondRequestCompany"));
                    mod.setBondInsuranceId(dObj.getString("BondInsuranceId"));
                    mod.setBondInsuranceName(dObj
                            .getString("BondInsuranceName"));
                    ArrayList<CompanyBidingModel> iList2 = new ArrayList<CompanyBidingModel>();
                    JSONArray iArr3 = dObj.getJSONArray("CompaniesBiddings");
                    if (iArr3 != null) {
                        for (int i = 0; i < iArr3.length(); i++) {
                            JSONObject iObj = iArr3.getJSONObject(i);
                            if (iObj != null) {
                                CompanyBidingModel iMod = new CompanyBidingModel();
                                iMod.setCompanyId(iObj.optString("CompanyId"));
                                iMod.setAmount(iObj.optString("Amount"));
                                iMod.setCompanyName(iObj
                                        .optString("CompanyName"));
                                iMod.setCompanyImage(PHOTO
                                        + iObj.optString("CompanyImage"));
                                iMod.setDescription(iObj
                                        .optString("Description"));
                                iMod.setAcceptedStatus(iObj
                                        .optString("AcceptedStatus"));
                                iMod.setRequestId(iObj.optString("RequestId"));
                                iMod.setBidingId(iObj.optString("bidding_id"));

                                iList2.add(iMod);
                            } else {
                                iList2.add(null);
                            }

                        }
                        AllBidListCompany.add(iList2);
                    } else {
                        AllBidListCompany.add(iList2);
                    }

                }
                mod.setSenderCompanyImage(PHOTO
                        + dObj.getString("ReceiverCompanyImage"));
                mod.setSenderCompanyName(dObj.getString("ReceiverCompanyName"));
                mod.setSenderCompanyId(dObj.getString("ReceiverCompanyId"));
                mod.setCompanyOfferToPay(dObj.getString("CompanyOfferToPay"));
                //
                // String str = dObj.getString("IsAccept") + "";
                // if (str.equalsIgnoreCase("1"))
                // mod.setIsAccept(true);
                // else
                // mod.setIsAccept(false);
                mod.setIsAccept(dObj.getString("IsAccept"));
                mod.setIsComplete(dObj.getString("IsComplete"));
                mod.setIsAbort(dObj.getString("IsAborted"));
                mod.setIsCancel(dObj.getString("IsCancel"));
                mod.setRead(dObj.getString("Read"));
                mod.setDefBookingNumber(dObj.getString("DefBookingNumber"));
                mod.setLocationLongitude(dObj.getString("LocationLongitude"));
                mod.setLocationLatitude(dObj.getString("LocationLatitude"));
                mod.setBondRequestCompany(dObj.getString("BondRequestCompany"));
                mod.setLocation(dObj.getString("Location"));
                mod.setPaymentPlan(dObj.getString("PaymentPlan"));
                mod.setCreatedDate(dObj.getString("CreatedDate"));
                mod.setInstructionForAgent(dObj
                        .getString("InstructionForAgent"));
                mod.setNumberIndemnitors(dObj.getString("NumberIndemnitors"));
                mod.setAmountToCollect(dObj.getString("AmountToCollect"));
                mod.setAgentImage(PHOTO + dObj.getString("AgentImage"));
                mod.setAgentName(dObj.getString("Name"));
                mod.setAgentId(dObj.getString("AgentId"));
                String temp;

                temp = dObj.getString("NeedCourtFee");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedCourtFee(true);
                } else {
                    mod.setNeedCourtFee(false);
                }
                temp = dObj.getString("NeedBailSource");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedBailSource(true);
                } else {
                    mod.setNeedBailSource(false);
                }
                temp = dObj.getString("IsCallAgency");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setIsCallAgency(true);
                } else {
                    mod.setIsCallAgency(false);
                }
                temp = dObj.getString("NeedPaperWork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedPaperWork(true);
                } else {
                    mod.setNeedPaperWork(false);
                }
                temp = dObj.getString("NeedIndemnitorPaperwork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedIndemnitorPaperwork(true);
                } else {
                    mod.setNeedIndemnitorPaperwork(false);
                }
                temp = dObj.getString("NeedDefendantPaperwork");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setNeedDefendantPaperwork(true);
                } else {
                    mod.setNeedDefendantPaperwork(false);
                }
                temp = dObj.getString("PaymentAlreadyReceived");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setPaymentAlreadyReceived(true);
                } else {
                    mod.setPaymentAlreadyReceived(false);
                }

                // String str = dObj.getString("IsAccept") + "";
                // mod.setIsAccept("true".equals(str));
                mod.setSentRequestTime(dObj.getString("SentRequestTime"));//

                JSONArray wArr = dObj.getJSONArray("WarrantList");
                ArrayList<WarrantModel> wList = new ArrayList<WarrantModel>();
                if (wArr != null && wArr.length() > 0) {
                    for (int i = 0; i < wArr.length(); i++) {
                        JSONObject wObj = wArr.getJSONObject(i);
                        if (wObj != null) {
                            WarrantModel wMod = new WarrantModel();
                            wMod.setAmount(wObj.optString("Amount"));
                            wMod.setTownship(wObj.optString("Township"));

                            wList.add(wMod);
                        }
                    }
                }
                mod.setWarrantList(wList);

                JSONArray iArr = dObj.getJSONArray("IndemnitorsList");
                ArrayList<IndemnitorModel> iList = new ArrayList<IndemnitorModel>();
                if (iArr != null && iArr.length() > 0) {
                    for (int i = 0; i < iArr.length(); i++) {
                        JSONObject iObj = iArr.getJSONObject(i);
                        if (iObj != null) {
                            IndemnitorModel iMod = new IndemnitorModel();
                            iMod.setName(iObj.optString("Name"));
                            iMod.setFName(iObj.optString("ind_firstname"));
                            iMod.setLName(iObj.optString("ind_lastname"));
                            iMod.setPhoneNumber(iObj.optString("PhoneNumber"));

                            iList.add(iMod);
                        }
                    }
                }
                mod.setIndemnitorsList(iList);

                JSONArray insArr = dObj.getJSONArray("InsuranceList");
                ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();
                if (insArr != null && insArr.length() > 0) {
                    for (int i = 0; i < insArr.length(); i++) {
                        JSONObject insObj = insArr.getJSONObject(i);
                        if (insObj != null) {
                            InsuranceModel insMod = new InsuranceModel();
                            insMod.setId(Integer.parseInt(insObj
                                    .optString("Id")));
                            insMod.setName(insObj.optString("Name"));

                            insList.add(insMod);
                        }
                    }
                }
                mod.setInsuranceList(insList);

                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static BailRequestModel parseReferBailRequestDetailReceiver(
            JSONObject dObj) {
        try {
            if (dObj != null) {
                BailRequestModel mod = new BailRequestModel();
                mod.setAgentRequestId(dObj.getInt("Id"));
                // mod.setId(dObj.getInt("Id"));
                mod.setDefendantName(dObj.getString("DefendantName"));
                mod.setDefDOB(dObj.getString("DefDOB"));
                if (HistoryRequestList.title
                        .equalsIgnoreCase("Sent Refer Bail Request")) {
                    mod.setSenderCompanyImage(PHOTO
                            + MainActivity.user.getPhoto());
                    mod.setSenderCompanyName(MainActivity.user.getName());
                    mod.setSenderCompanyId(MainActivity.user.getCompanyId());

                } else {
                    mod.setSenderCompanyImage(PHOTO
                            + dObj.getString("ReceiverCompanyImage"));
                    mod.setSenderCompanyName(dObj
                            .getString("ReceiverCompanyName"));
                    mod.setSenderCompanyId(dObj.getString("ReceiverCompanyId"));
                }
                mod.setRead(dObj.getString("Read"));
                mod.setLocationLongitude(dObj.getString("LocationLongitude"));
                mod.setLocationLatitude(dObj.getString("LocationLatitude"));
                mod.setBondRequestCompany(dObj.getString("BondRequestCompany"));
                mod.setLocation(dObj.getString("Location"));
                mod.setPaymentPlan(dObj.getString("PaymentPlan"));
                mod.setCreatedDate(dObj.getString("CreatedDate"));
                mod.setInstructionForAgent(dObj
                        .getString("InstructionForAgent"));
                mod.setNumberIndemnitors(dObj.getString("NumberofCosigner"));
                mod.setAmountToCollect(dObj.getString("AmountToCollect"));
                mod.setAmmountForCommission(dObj
                        .getString("AmountForCommission"));

                mod.setAgentId(dObj.getString("AgentId"));
                String temp;

                temp = dObj.getString("PaymentAlreadyReceived");
                if (temp.equalsIgnoreCase("1")) {
                    mod.setPaymentAlreadyReceived(true);
                } else {
                    mod.setPaymentAlreadyReceived(false);
                }

                // String str = dObj.getString("IsAccept") + "";
                // mod.setIsAccept("true".equals(str));
                mod.setSentRequestTime(dObj.getString("SentRequestTime"));//

                JSONArray wArr = dObj.getJSONArray("WarrantList");
                ArrayList<WarrantModel> wList = new ArrayList<WarrantModel>();
                if (wArr != null && wArr.length() > 0) {
                    for (int i = 0; i < wArr.length(); i++) {
                        JSONObject wObj = wArr.getJSONObject(i);
                        if (wObj != null) {
                            WarrantModel wMod = new WarrantModel();
                            wMod.setAmount(wObj.optString("Amount"));
                            wMod.setTownship(wObj.optString("Township"));

                            wList.add(wMod);
                        }
                    }
                }
                mod.setWarrantList(wList);

                JSONArray insArr = dObj.getJSONArray("InsuranceList");
                ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();
                if (insArr != null && insArr.length() > 0) {
                    for (int i = 0; i < insArr.length(); i++) {
                        JSONObject insObj = insArr.getJSONObject(i);
                        if (insObj != null) {
                            InsuranceModel insMod = new InsuranceModel();
                            insMod.setId(Integer.parseInt(insObj
                                    .optString("Id")));
                            insMod.setName(insObj.optString("Name"));

                            insList.add(insMod);
                        }
                    }
                }
                mod.setInsuranceList(insList);
                ArrayList<CompanyBidingModel> iList2 = new ArrayList<CompanyBidingModel>();
                JSONArray iArr3 = dObj.getJSONArray("CompaniesBiddings");
                if (iArr3 != null) {
                    for (int i = 0; i < iArr3.length(); i++) {

                        JSONObject iObj = iArr3.getJSONObject(i);
                        if (iObj != null) {
                            CompanyBidingModel iMod = new CompanyBidingModel();
                            iMod.setCompanyId(iObj.optString("CompanyId"));
                            iMod.setAmount(iObj.optString("Amount"));
                            iMod.setCompanyName(iObj.optString("CompanyName"));
                            iMod.setCompanyImage(PHOTO
                                    + iObj.optString("CompanyImage"));
                            iMod.setDescription(iObj.optString("Description"));
                            iMod.setAcceptedStatus(iObj
                                    .optString("AcceptedStatus"));
                            iMod.setRequestId(iObj.optString("RequestId"));
                            iMod.setBidingId(iObj.optString("bidding_id"));

                            iList2.add(iMod);
                        } else {
                            iList2.add(null);
                        }

                    }
                    AllBidListCompany.add(iList2);
                } else {
                    AllBidListCompany.add(iList2);
                }
                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static BadDebtMember parseBadBedtRequestDetail(JSONObject dObj) {
        try {
            if (dObj != null) {
                BadDebtMember mod = new BadDebtMember();

                mod.setBadDebtMemberId(dObj.getString("BadDebtMemberId"));
                mod.setCompanyId(dObj.getString("CompanyId"));
                mod.setCompanyName(dObj.getString("CompanyName"));
                mod.setCompanyPicture(PHOTO + dObj.getString("CompanyPicture"));
                mod.setName(dObj.optString("Name"));
                mod.setPicture(PHOTO + dObj.optString("Picture"));
                mod.setAddress(dObj.getString("Address"));
                mod.setCity(dObj.getString("City"));
                mod.setState(dObj.getString("State"));
                mod.setZip(dObj.getString("Zip"));
                mod.setTelephone(dObj.getString("Telephone"));
                mod.setDOB(dObj.getString("DOB"));
                // mod.setRead(dObj.getString("Read"));
                mod.setDefendentOrCosigner(dObj
                        .getString("DefendentOrCosigner"));
                mod.setAmountOwed(dObj.getString("AmountOwed"));
                mod.setCreatedDateTime(dObj.getString("CreatedDateTime"));

                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Payment parsePaymentDetail(JSONObject dObj) {
        try {
            if (dObj != null) {
                Payment mod = new Payment();

                mod.setNameOnCard(dObj.getString("NameOnCard"));
                mod.setCardNumber(dObj.getString("CardNumber"));
                mod.setCvv(dObj.getString("CVV"));
                mod.setExDate(dObj.getString("ExpiryDate"));
                mod.setCardType(dObj.optString("CardType"));
                mod.setNameOnAccount(dObj.optString("NameOnAccount"));
                mod.setAccType(dObj.getString("AccountType"));
                mod.setAccNumber(dObj.getString("AccountNumber"));
                mod.setRoutingNumber(dObj.getString("RoutingNumber"));
                mod.setCreadtedDate(dObj.getString("CreatedDate"));
                mod.setIncNumber(dObj.getString("IncCardNumber"));

                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static BlackListMember parseBlackListRequestDetail(JSONObject dObj) {
        try {
            if (dObj != null) {
                BlackListMember mod = new BlackListMember();

                mod.setBlacklistMemberId(dObj.getString("BlacklistMemberId"));
                mod.setCompanyId(dObj.getString("CompanyId"));
                mod.setCompanyName(dObj.getString("CompanyName"));
                mod.setCompanyPicture(PHOTO + dObj.getString("CompanyPicture"));
                mod.setNameOfDefendent(dObj.optString("NameOfDefendent"));
                mod.setPicture(PHOTO + dObj.optString("Picture"));
                mod.setAddress(dObj.getString("Address"));
                mod.setCity(dObj.getString("City"));
                mod.setState(dObj.getString("State"));
                mod.setZip(dObj.getString("Zip"));
                // mod.setRead(dObj.getString("Read"));
                mod.setTelephone(dObj.getString("Telephone"));
                mod.setAmountForfeited(dObj.getString("AmountForfeited"));
                mod.setDOB(dObj.getString("DOB"));
                mod.setDOF(dObj.getString("DOF"));
                mod.setNotes(dObj.getString("Notes"));
                mod.setCreatedDateTime(dObj.getString("CreatedDateTime"));

                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<InsuranceModel> getCompanyInsurances(String compId) {
        ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
        param.add(new BasicNameValuePair("CompanyId", compId));
        // String resp = executePostRequest(GET_COMPANY_INSURANCES, param,
        // false);
        String resp = executePostRequest(GET_ALL_INSURANCES, param, false);
        Log.d("ins", resp + "");
        ArrayList<InsuranceModel> insList = new ArrayList<InsuranceModel>();

        try {
            if (!Commons.isEmpty(resp)) {
                JSONObject json = new JSONObject(resp);
                JSONArray arr = json.getJSONArray("list_of_insurances");
                if (json.optString("status").equalsIgnoreCase("1")
                        && arr != null && arr.length() > 0) {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        if (obj != null) {
                            InsuranceModel im = new InsuranceModel();
                            im.setId(obj.optInt("Id"));
                            im.setName(obj.optString("Name"));
                            im.setCreatedDate(obj.optString("CreatedDate"));
                            im.setCompanyName(obj.optString("CompanyName"));
                            im.setInsAddress(obj.optString("InsAddress"));
                            im.setAPT(obj.optString("APT"));
                            im.setTown(obj.optString("Town"));
                            im.setInsState(obj.optString("InsState"));
                            im.setZip(obj.optString("Zip"));
                            im.setBusinessPhone(obj.optString("BusinessPhone"));
                            im.setContatcPerson(obj.optString("ContatcPerson"));
                            im.setEmail(obj.optString("Email"));
                            im.setLicenseNo(obj.optString("LicenseNo"));
                            im.setForAgent(obj.optBoolean("ForAgent"));

                            insList.add(im);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return insList;
    }

    public static User getResponse(String response) {

        try {
            if (!Commons.isEmpty(response)) {
                Log.e("Output Response 1FB :", response);
                JSONObject json = new JSONObject(response);
                if (json.optString("status").equalsIgnoreCase("1")) {
                    JSONObject obj = json.getJSONObject("data");
                    ArrayList<String> insur = new ArrayList<String>();
                    ArrayList<String> insurName = new ArrayList<String>();
                    ArrayList<String> states = new ArrayList<String>();
                    ArrayList<String> statesName = new ArrayList<String>();
                    u = new User();
                    u.setPackageId(obj.optString("package_id"));
                    u.setAddonOneEnabled(obj.optString("addon_one_enabled"));
                    u.setAddonTwoEnabled(obj.optString("addon_two_enabled"));
                    u.setTotalPaymentForLastSubscription(obj
                            .optString("total_payment_for_last_subscription"));
                    u.setSubscriptionStartTime(obj
                            .optString("subscription_start_time"));
                    u.setSubscriptionEndTime(obj
                            .optString("subscription_end_time"));
                    u.setName(obj.optString("ownername"));
                    u.setCompanyName(obj.optString("CompanyName"));
                    u.setPackageExpired(obj.optString("PackageExpired"));
                    u.setRolename(obj.optString("rolename"));
                    u.setUsercode(obj.optString("UserCode"));
                    u.setUsername(obj.optString("username"));
                    u.setCity(obj.optString("city"));
                    u.setState(obj.optString("state"));
                    u.setCountry(obj.optString("country"));
                    u.setLicenseExpi(obj.optString("licenseExpi"));
                    u.setLisencestate(obj.optString("lisencestate"));
                    u.setCompanyApprovalStatus(obj
                            .optString("CompanyApprovalStatus"));
                    u.setCanSeeAgentState(obj.optString("CanSeeAgentState"));
                    u.setCanSeeAgentInsurance(obj
                            .optString("CanSeeAgentInsurance"));
                    u.setZipCode(obj.optString("ZIP"));
                    u.setAPT(obj.optString("APT"));
                    u.setBusinessPhone(obj.optString("BusinessPhone"));
                    u.setDeviceId(obj.optString("DeviceId"));
                    u.setCompanyId(obj.optString("companyid"));
                    u.setEmail(obj.optString("email"));
                    u.setPassword(obj.optString("password"));
                    u.setAddress(obj.optString("address"));
                    u.setLicenseno(obj.optString("licenseno"));
                    u.setPhone(obj.optString("phone"));
                    u.setTempAccessCode(obj.optString("TemporaryAccessCode"));
                    u.setPhoto(PHOTO + obj.optString("photo"));
                    if (obj.getJSONArray("insurance").length() > 0) {
                        for (int j = 0; j < obj.getJSONArray("insurance")
                                .length(); j++) {
                            insur.add(obj.getJSONArray("insurance")
                                    .optString(j));
                        }
                        u.setInsurance(insur);
                    } else
                        u.setInsurance(null);

                    if (obj.getJSONArray("states").length() > 0) {
                        for (int j = 0; j < obj.getJSONArray("states").length(); j++) {
                            states.add(obj.getJSONArray("states").optString(j));
                        }
                        u.setStates(states);
                    } else
                        u.setStates(null);

                    if (obj.getJSONArray("insurancesName").length() > 0) {
                        for (int j = 0; j < obj.getJSONArray("insurancesName")
                                .length(); j++) {
                            insurName.add(obj.getJSONArray("insurancesName")
                                    .optString(j));
                        }
                        u.setInsurancesName(insurName);
                    } else
                        u.setInsurancesName(null);

                    if (obj.getJSONArray("statesName").length() > 0) {
                        for (int j = 0; j < obj.getJSONArray("statesName")
                                .length(); j++) {
                            statesName.add(obj.getJSONArray("statesName")
                                    .optString(j));
                        }
                        u.setStatesName(statesName);
                    } else
                        u.setStatesName(null);

                    return u;
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doRegister(User user) {

        JSONObject obj = new JSONObject();
        JSONArray insur = new JSONArray();
        try {
            if (user.getInsurance() != null) {
                for (String s : user.getInsurance()) {
                    insur.put(s);
                }
            }
            obj.put("CVV", user.getCVV());
            obj.put("CardExpiry", user.getCardExpiry());
            obj.put("CardNumber", user.getCardNumber());
            obj.put("CardType", "visa");
            obj.put("CompanyName", user.getCompanyName());
            obj.put("Insurance", insur);
            obj.put("NameOnCard", user.getNameOnCard());
            obj.put("ZipCode", user.getZipCode());
            obj.put("address", user.getAddress());
            obj.put("city", user.getCity());
            obj.put("country", user.getCountry());
            obj.put("email", user.getEmail());
            obj.put("licenseno", user.getLicenseno());
            obj.put("name", user.getName());
            obj.put("password", user.getPassword());
            obj.put("phone", user.getPhone());
            obj.put("photo",
                    Commons.isEmpty(user.getPhoto()) ? "" : user.getPhoto());
            obj.put("state", user.getState());
            obj.put("username", user.getUsername());

            String stRes = executeArray(REGISTER_URL, obj);
            if (!Commons.isEmpty(stRes)) {
                Log.e("Output Response 2:", stRes);
                JSONObject json = new JSONObject(stRes);
                if (json.optString("message").equalsIgnoreCase("success"))
                    return "done";
                else
                    return json.optString("message");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // public static String forgotPassword(String email) {
    // ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
    // param.add(new BasicNameValuePair("UserEmail", email));
    //
    // String stRes = executePostRequest(FORGOT_PASS, param, false);
    // try {
    // if (!Commons.isEmpty(stRes)) {
    // Log.e("Output Response 3FORGOT:", stRes);
    // JSONObject json = new JSONObject(stRes);
    // if (json.optString("message").equals("success"))
    // return "done";
    // else
    // return "invalid";
    //
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return null;
    // }

    // public static ArrayList<AgentModel> getAllAgent(String CompanyId) {
    // ArrayList<AgentModel> agentList = new ArrayList<AgentModel>();
    // ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
    // param.add(new BasicNameValuePair("CompanyId", CompanyId));
    // String stRes = executePostRequest(GET_ALL_AGENT, param, false);
    // try {
    // if (!Commons.isEmpty(stRes)) {
    // Log.e("Output Response 4getAllAgent:", stRes);
    // JSONObject json = new JSONObject(stRes);
    // JSONArray jArray = json.getJSONArray("data");
    //
    // return parseAgentList(jArray);
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return null;
    // }

    private static String executeArray(String restUrl, Object obj) {
        try {
            // String MAIN_URL =
            // "http://service.wadlinstabail.com/taxiwcf.svc/";
            HttpPost post = new HttpPost(MAIN_URL + restUrl);
            post.addHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            if (obj != null) {
                StringEntity entity = new StringEntity(obj.toString(), "UTF-8");
                Log.e("Request", obj.toString());
                entity.setContentType("application/json");
                post.setEntity(entity);
            }

            HttpResponse res = new DefaultHttpClient().execute(post);
            if (res != null) {
                String strRes = EntityUtils.toString(res.getEntity());
                return strRes;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<AgentModel> parseAgentList(JSONArray jArray) {
        ArrayList<AgentModel> agentList = new ArrayList<AgentModel>();
        try {
            if (jArray.length() > 0) {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject js = jArray.getJSONObject(i);
                    AgentModel agent = new AgentModel();
                    agent.setUsername(js.optString("username"));
                    agent.setUserCode(js.optString("UserCode"));
                    agent.setPassword(js.optString("password"));
                    agent.setRolename(js.optString("rolename"));
                    agent.setCreateddate(js.optString("createddate"));
                    agent.setPhotoUrl(PHOTO + js.optString("photo"));
                    agent.setCity(js.optString("city"));
                    agent.setState(js.optString("state"));
                    agent.setCountry(js.optString("country"));
                    agent.setPhone(js.optString("phone"));
                    agent.setEmail(js.optString("email"));
                    agent.setLicenseNo(js.optString("licenseno"));
                    agent.setLicenseExpire(js.optString("licenseExpi"));
                    agent.setLicenseState(js.optString("lisencestate"));
                    agent.setAgentApprovalStatus(js
                            .optString("AgentApprovalStatus"));
                    agent.setCardNumber(js.optString("CardNumber"));
                    agent.setCardExpiry(js.optString("CardExpiry"));
                    agent.setNameOnCard(js.optString("NameOnCard"));
                    agent.setCardType(js.optString("CardType"));
                    agent.setCompanyname(js.optString("companyname"));
                    agent.setAPT(js.optString("APT"));
                    agent.setZIP(js.optString("ZIP"));
                    agent.setDeviceId(js.optString("DeviceId"));
                    agent.setAgentId(js.optString("agentid"));
                    agent.setIsOnline(js.optString("IsOnline"));
                    agent.setAgentName(js.optString("agentname"));
                    agent.setAddress(js.optString("address"));
                    agent.setLongitude(js.optString("Longitude"));
                    agent.setLatitude(js.optString("Latitude"));
                    agent.setInstabailAgents(js.optString("InstabailAgent"));
                    agent.setLocationLongitude(js
                            .optString("LocationLongitude"));
                    agent.setLocationLatitude(js.optString("LocationLatitude"));
                    if (hireReq) {
                        agent.setReqStatus("Hired");
                        JSONObject jsWorking = js
                                .optJSONObject("working_request");
                        if (jsWorking != null) {

                            defendantDetail.add(jsWorking.optString("Id"));
                        } else {
                            defendantDetail.add(null);
                        }
                    } else {
                        String str = js.optString("IsAccept") + "";
                        if (str.equalsIgnoreCase("true"))
                            agent.setReqStatus("ACCEPTED");
                        else if (str.equalsIgnoreCase("false"))
                            agent.setReqStatus("REJECTED");
                        else
                            agent.setReqStatus("PENDING");
                    }
                    // JSONArray insArr = js.getJSONArray("insurance");
                    // ArrayList<String> insList = new ArrayList<String>();
                    // if (insArr != null && insArr.length() > 0) {
                    // for (int k = 0; k < insArr.length(); k++) {
                    // insList.add(insArr.getString(k));
                    // }
                    // }
                    // agent.setInsuranceList(insList);
                    //
                    // JSONArray insNameArr = js.getJSONArray("insurancesName");
                    // ArrayList<String> insNameList = new ArrayList<String>();
                    // if (insNameArr != null && insNameArr.length() > 0) {
                    // for (int k = 0; k < insNameArr.length(); k++) {
                    // insNameList.add(insNameArr.getString(k));
                    // }
                    // }
                    // agent.setInsurancesName(insNameList);
                    //
                    // JSONArray stateArr = js.getJSONArray("states");
                    // ArrayList<String> stateList = new ArrayList<String>();
                    // if (stateArr != null && stateArr.length() > 0) {
                    // for (int k = 0; k < stateArr.length(); k++) {
                    // stateList.add(stateArr.getString(k));
                    // }
                    // }
                    // agent.setStates(stateList);
                    //
                    // JSONArray stateNameArr = js.getJSONArray("statesName");
                    // ArrayList<String> stateNameList = new
                    // ArrayList<String>();
                    // if (stateNameArr != null && stateNameArr.length() > 0) {
                    // for (int k = 0; k < stateNameArr.length(); k++) {
                    // stateNameList.add(stateNameArr.getString(k));
                    // }
                    // }
                    // agent.setStatesName(stateNameList);

                    agentList.add(agent);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return agentList;
    }

    public static ArrayList<AgentModel> getFindBestAgent(JSONObject obj) {
        // ArrayList<AgentModel> agentList = new ArrayList<AgentModel>();
        String stRes = executeArray(GET_AN_AGENT, obj);
        try {
            if (!Commons.isEmpty(stRes)) {
                Log.e("Output AGENT:", stRes);
                JSONObject json = new JSONObject(stRes);
                JSONObject j = json.getJSONObject("data");
                agentRequestId = j.optString("AgentRequestId");
                JSONArray jArray = j.getJSONArray("Agents");

                return parseAgentList(jArray);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int sendRequestToAgent(String agentId, String agentRequestId) {

        ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
        param.add(new BasicNameValuePair("AgentId", agentId));
        param.add(new BasicNameValuePair("RequestId", agentRequestId));
        param.add(new BasicNameValuePair("TemporaryAccessCode",
                MainActivity.user.getTempAccessCode()));
        param.add(new BasicNameValuePair("UserName", MainActivity.user
                .getUsername()));
        String stRes = executePostRequest(SEND_REQUEST_AGENT, param, false);
        try {
            if (!Commons.isEmpty(stRes)) {
                Log.e("OutputAGENT:", stRes);
                JSONObject obj = new JSONObject(stRes);
                if (obj != null) {
                    String status = obj.getString("status");
                    if (!Commons.isEmpty(status))
                        return Integer.parseInt(status);
                }

            }
        } catch (Exception e) {

        }

        return 0;
    }

    public static PackageModel packageDetail(JSONObject dObj) {
        try {
            if (dObj != null) {

                PackageModel mod = new PackageModel();
                mod.setPackageID(dObj.getString("package_id"));
                mod.setPackageName(dObj.getString("package_name"));
                mod.setPackageDiscription(dObj.getString("package_description"));

                mod.setPackageType(dObj.getString("package_type"));
                mod.setPackacgeDuration(dObj
                        .getString("package_duration_in_days"));
                mod.setPackagePrice(dObj.getString("package_price"));
                mod.setPerRequestPrice(dObj.getString("per_request_price"));
                mod.setSupportsAddons(dObj.getString("supports_addons"));
                mod.setPackageStatus(dObj.getString("package_status"));
                mod.setUpdatedAt(dObj.getString("updated_at"));
                mod.setCreateddAt(dObj.getString("created_at"));
                JSONArray array = dObj.getJSONArray("addons");
                ArrayList<AddOns> addOns = new ArrayList<AddOns>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    AddOns addOn = new AddOns();
                    addOn.setAddonId(jsonObject.getString("addon_id"));
                    addOn.setAddonTitle(jsonObject.getString("addon_title"));
                    addOn.setAddonDescription(jsonObject
                            .getString("addon_description"));
                    addOn.setAddonPrice(jsonObject.getString("addon_price"));
                    addOn.setUpdatedAt(jsonObject.getString("addon_price"));
                    addOn.setCreatedAt(jsonObject.getString("created_at"));
                    addOns.add(addOn);
                }
                mod.setAddOns(addOns);

                return mod;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static AgentModel getAgent(String stRes) {

        try {
            if (!Commons.isEmpty(stRes)) {
                ArrayList<String> insur = new ArrayList<String>();
                ArrayList<String> states = new ArrayList<String>();
                Log.e("Output Response1 :", stRes);
                JSONObject json = new JSONObject(stRes);
                if (json.optString("status").equalsIgnoreCase("1")) {
                    JSONObject js = json.getJSONObject("agent_details");
                    AgentModel loginRes = new AgentModel();
                    loginRes.setAgentId(js.optString("agentid"));
                    loginRes.setIsOnline(js.optString("IsOnline"));
                    loginRes.setAgentName(js.optString("agentname"));
                    loginRes.setUsername(js.optString("username"));
                    loginRes.setEmail(js.optString("email"));
                    loginRes.setAddress(js.optString("address"));
                    loginRes.setPhone(js.optString("phone"));
                    loginRes.setLicenseNo(js.optString("licenseno"));
                    loginRes.setLicenseExpire(Commons.millsToDate(Commons
                            .jsonDateToTimeMillis(js.optString("licenseExpi"))));
                    loginRes.setAgentApprovalStatus(js
                            .optString("AgentApprovalStatus"));
                    loginRes.setCardNumber(js.optString("CardNumber"));
                    loginRes.setCardExpiry(js.optString("CardExpiry"));
                    loginRes.setPhotoUrl(PHOTO + js.optString("photo"));
                    loginRes.setLicenseState(js.optString("statesName")
                            .replace("[", "").replace("]", ""));
                    JSONArray jArray = js.getJSONArray("insurancesName");
                    if (jArray.length() > 0) {
                        for (int i = 0; i < jArray.length(); i++) {
                            insur.add(jArray.optString(i));
                        }
                    }
                    loginRes.setInsuranceList(insur);
                    try {
                        if (js.getJSONArray("statesName").length() > 0) {
                            for (int j = 0; j < js.getJSONArray("statesName")
                                    .length(); j++) {
                                states.add(js.getJSONArray("statesName")
                                        .optString(j));
                            }
                            loginRes.setStatesName(states);
                        } else
                            loginRes.setStatesName(null);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    return loginRes;
                } else
                    return null;

            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // public static String contactUs(String email, String name, String query) {
    // ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
    // param.add(new BasicNameValuePair("Email", email));
    // param.add(new BasicNameValuePair("Name", name));
    // param.add(new BasicNameValuePair("Query", query));
    // String stRes = executePostRequest(CONTACT_US, param, false);
    // try {
    // if (!Commons.isEmpty(stRes)) {
    // Log.e("Output Response 9CONTACT_US:", stRes);
    // JSONObject json = new JSONObject(stRes);
    // if (json.optString("status").equalsIgnoreCase("1"))
    // return "done";
    //
    // }
    // } catch (Exception e) {
    //
    // }
    // return null;
    // }
}