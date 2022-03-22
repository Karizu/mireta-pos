package com.boardinglabs.mireta.standalone.modul.pickitems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.boardinglabs.mireta.standalone.BaseActivity;
import com.boardinglabs.mireta.standalone.R;
import com.boardinglabs.mireta.standalone.component.adapter.OverviewItemVarianAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.PickItemsAdapter;
import com.boardinglabs.mireta.standalone.component.adapter.RecyOverviewNewItemsAdapter;
import com.boardinglabs.mireta.standalone.component.fontview.RobotoRegularTextView;
import com.boardinglabs.mireta.standalone.component.listener.ItemActionListener;
import com.boardinglabs.mireta.standalone.component.listener.ListActionListener;
import com.boardinglabs.mireta.standalone.component.network.NetworkManager;
import com.boardinglabs.mireta.standalone.component.network.NetworkService;
import com.boardinglabs.mireta.standalone.component.network.entities.Item;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.GroupItem;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemVariants;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.Variant;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionPost;
import com.boardinglabs.mireta.standalone.component.network.entities.TransactionToCashier;
import com.boardinglabs.mireta.standalone.component.util.MethodUtil;
import com.boardinglabs.mireta.standalone.component.util.PreferenceManager;
import com.boardinglabs.mireta.standalone.modul.CommonInterface;
import com.boardinglabs.mireta.standalone.modul.home.HomeActivity;
import com.boardinglabs.mireta.standalone.modul.pickitems.payment.PaymentActivity;
import com.boardinglabs.mireta.standalone.modul.transactions.items.ItemsPresenter;
import com.boardinglabs.mireta.standalone.modul.transactions.items.ItemsView;
import com.boardinglabs.mireta.standalone.modul.transactions.items.pembayaran.PembayaranActivity;
import com.google.gson.Gson;
import com.paging.gridview.PagingGridView;
import com.paging.listview.PagingListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

public class PickItemsActivity extends BaseActivity implements ItemsView, CommonInterface, ItemActionListener, ListActionListener {
    private ItemsPresenter mPresenter;

    protected RecyclerView reportRecyclerView;

    private List<Item> items;
    private List<ItemVariants> orederditems;
    private List<ItemVariants> orderItemUpdatedList;
    private List<ItemVariants> itemsResponseList;
    private PagingListView overviewListView;
    private PickItemsAdapter itemsAdapter;
    private RecyOverviewNewItemsAdapter overviewItemsAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private int currentPage;
    private int total_post;
    private int transactionType, spesificMethod, spesificItemTypeId;

    private LinearLayout collapsed_view;
    private LinearLayout expanded_view;
    private ConstraintLayout overview_view;
    private ImageButton bottom_button;
    private TextView total_item;
    private TextView total_price;
    private TextView total_price_overview;
    private TransactionPost transactionPost;
    private List<TransactionPost.Items> transactionItems;
    private TransactionToCashier transactionToCashier;
    private List<TransactionToCashier.Items> itemsList;
    private String listOrder;
    private long mTotalPrice;
    private Context context;
    private BottomSheetDialog dialogBottom;
    private OverviewItemVarianAdapter adapter;

    private boolean expanded = false;
    private boolean isGridView = false;
    private RecyclerView rvOverviewVariant;
    private TextView tvTitle, tvPrice;

    @BindView(R.id.laySearch)
    LinearLayout laySearch;
    @BindView(R.id.header_view)
    LinearLayout header_view;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.item_name)
    RobotoRegularTextView item_name;
    @BindView(R.id.imgSearch)
    ImageView imgSearch;
    @BindView(R.id.btn_grid_list)
    ImageView btn_grid_list;
    @BindView(R.id.item_list)
    PagingListView itemsListView;
    @BindView(R.id.item_grid)
    PagingGridView itemsGridView;

    @OnClick(R.id.btn_grid_list)
    void onClickGridList(){
        if (!isGridView){
            isGridView = true;
            itemsGridView.setVisibility(View.VISIBLE);
            itemsListView.setVisibility(View.GONE);
            itemsAdapter = new PickItemsAdapter(true);
            itemsAdapter.setListener(this);
            itemsGridView.setAdapter(itemsAdapter);
            btn_grid_list.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
        } else {
            isGridView = false;
            itemsListView.setVisibility(View.VISIBLE);
            itemsGridView.setVisibility(View.GONE);
            itemsAdapter = new PickItemsAdapter(false);
            itemsAdapter.setListener(this);
            itemsListView.setAdapter(itemsAdapter);
            btn_grid_list.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
        }

        loadTransactionsData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ItemsPresenter(this, this);
        context = this;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_pick_item;
    }

    @Override
    protected void setContentViewOnChild() {
        ButterKnife.bind(this);
        setToolbarTitle("Tambah Transaksi");
        initComponent();
    }

    @Override
    protected void onCreateAtChild() {
        mPresenter = new ItemsPresenter(this, this);

    }

    @Override
    protected void onBackBtnPressed() {
        onBackPressed();
    }

    @Override
    protected void onSubmitBtnPressed() {

    }

    public void clickDetailVariant(ItemVariants itemVariants){
        String json = new Gson().toJson(itemVariants);
        Intent intent = new Intent(this, PickVariantsActivity.class);
        intent.putExtra("json", json);
        startActivityForResult(intent, 99);
    }

    @SuppressLint("SetTextI18n")
    public void setDialogData(View v1, ItemVariants itemVariants){
        showBottomDialog(v1);

        tvTitle = dialogBottom.findViewById(R.id.tvTitle);
        tvPrice = dialogBottom.findViewById(R.id.tvPrice);
        Button btnTambah = dialogBottom.findViewById(R.id.btnSimpan);
        btnTambah.setOnClickListener(v -> {
            dialogBottom.dismiss();
            clickDetailVariant(itemVariants);
        });

        rvOverviewVariant = dialogBottom.findViewById(R.id.rvOverviewVariant);

        int totalPrice = 0;
        List<ItemVariants> itemVariantsList = new ArrayList<>();
        for (ItemVariants variants : orederditems) {
            if (variants.getId().equals(itemVariants.getId())) {
                itemVariantsList.add(variants);
                totalPrice = totalPrice + (Integer.parseInt(variants.getPrice()) * variants.getOrderQty());
            }
        }

        Objects.requireNonNull(tvTitle).setText(itemVariants.getName());

        if (itemVariantsList.size() > 0) {
            adapter = new OverviewItemVarianAdapter(itemVariantsList, context);
            rvOverviewVariant.setAdapter(adapter);
            tvPrice.setText(MethodUtil.toCurrencyFormat(totalPrice + ""));
        } else {
            tvPrice.setText(MethodUtil.toCurrencyFormat(itemVariants.getPrice() + ""));
        }

    }

    private void setUpOverview(ItemVariants itemVariants){
        int totalPrice = 0;
        List<ItemVariants> itemVariantsList = new ArrayList<>();
        for (ItemVariants variants : orederditems) {
            if (variants.getId().equals(itemVariants.getId())) {
                itemVariantsList.add(variants);
                totalPrice = totalPrice + (Integer.parseInt(variants.getPrice()) * variants.getOrderQty());
            }
        }

        if (itemVariantsList.size() > 0) {
            adapter = new OverviewItemVarianAdapter(itemVariantsList, context);
            rvOverviewVariant.setAdapter(adapter);
            Objects.requireNonNull(tvTitle).setText(itemVariants.getName());
            tvPrice.setText(MethodUtil.toCurrencyFormat(totalPrice + ""));
        }
    }

    public boolean isItemOrdered(ItemVariants itemVariants){
        if (orederditems.size() > 0) {
            for (ItemVariants variants : orederditems) {
                if (variants.getId().equals(itemVariants.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private void showBottomDialog(View view){
        View viewSheet = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_bottom_variant_item, null);
        Log.d( "onClick: ",String.valueOf(viewSheet));
        dialogBottom = new BottomSheetDialog(view.getContext());
        dialogBottom.setContentView(viewSheet);
        dialogBottom.show();
    }


    private void initComponent() {
        btn_grid_list.setVisibility(View.VISIBLE);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            loadTransactionsData();
            pullToRefresh.setRefreshing(false);
        });

        if (getIntent()!=null){
            transactionType = getIntent().getIntExtra("transaction_type", 0);
            spesificMethod = getIntent().getIntExtra("spesific_method", 0);
            spesificItemTypeId = getIntent().getIntExtra("specific_item_transaction_type_id", 0);

        }

        orederditems = new ArrayList<>();
        orderItemUpdatedList = new ArrayList<>();

        itemsResponseList = new ArrayList<>();

        total_post = 0;
        currentPage = 0;

        itemsAdapter = new PickItemsAdapter(false);
        itemsAdapter.setListener(this);

        itemsGridView.setAdapter(itemsAdapter);
        itemsGridView.setPagingableListener(() -> {
            itemsGridView.onFinishLoading(false, null);
        });

        itemsGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (itemsGridView.getChildAt(0) != null) {
                    pullToRefresh.setEnabled(itemsGridView.getFirstVisiblePosition() == 0 && itemsGridView.getChildAt(0).getTop() == 0);
                }
            }
        });

        itemsListView.setAdapter(itemsAdapter);
        itemsListView.setPagingableListener(new PagingListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                itemsListView.onFinishLoading(false, null);
            }
        });
        itemsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (itemsListView.getChildAt(0) != null) {
                    pullToRefresh.setEnabled(itemsListView.getFirstVisiblePosition() == 0 && itemsListView.getChildAt(0).getTop() == 0);
                }
            }
        });
        loadTransactionsData();

        overviewItemsAdapter = new RecyOverviewNewItemsAdapter();
        overviewListView = findViewById(R.id.item_list_overview);
        overviewListView.setAdapter(overviewItemsAdapter);
        overviewItemsAdapter.setListener(this);

        collapsed_view =  (LinearLayout) findViewById(R.id.collapsed_view);
        expanded_view =  (LinearLayout) findViewById(R.id.expanded_view);
        overview_view = (ConstraintLayout) findViewById(R.id.overview_view);
        bottom_button = (ImageButton) findViewById(R.id.bottom_button);
        total_item = (TextView) findViewById(R.id.total_item);
        total_price = (TextView) findViewById(R.id.total_price);
        total_price_overview = (TextView) findViewById(R.id.total_price_overview);
        total_item = (TextView) findViewById(R.id.total_item);

        bottom_button.setOnClickListener(view -> {
            if (total_item.getText().toString().equals("0 Item") || total_item.getText().toString().equals("0 x")){
                Toast.makeText(PickItemsActivity.this, "Silahkan pesan item yang anda inginkan", Toast.LENGTH_SHORT).show();
            } else {
                expandBottomViewTo(!expanded);
            }
        });
        overview_view.setOnClickListener(view -> expandBottomView(false));
        expandBottomView(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == 99) {
            if (resultCode == RESULT_OK) {
                // Get String data from Intent
                String returnString = data.getStringExtra("json");
                ItemVariants variants = new Gson().fromJson(returnString, ItemVariants.class);
                addDataFromVariants(variants);
                updateData(variants);

                Log.d("Item Variant", returnString);
            }
        }
    }

    private void updateData(ItemVariants variants){
        setUpOverview(variants);
//        int qty = 0;
//        if (orederditems.size()>0){
//            for (ItemVariants orderItems : orederditems){
//                if (orderItems.getId().equals(variants.getId())) {
//                    qty = qty + orderItems.getOrderQty();
//                    variants.setOrderQty(qty);
//                }
//            }
//
//            Log.d("Qty", qty+" Pcs");
//        }

//        List<ItemVariants> itemVariantsList = itemsAdapter.itemList;
//        for (int i = 0; i < itemVariantsList.size(); i++) {
//            ItemVariants itemVariants = itemVariantsList.get(i);
//            if (itemVariants.getId().equals(variants.getId())){
//                itemVariants.setOrderQty(variants.getOrderQty());
//                itemsAdapter.notifyDataSetChanged();
//                break;
//            }
//        }
    }

    private void addDataFromVariants(ItemVariants variants){
        orederditems.add(variants);
        orderItemUpdatedList.clear();
        orderItemUpdatedList.addAll(orederditems);
        orederditems.clear();
        orederditems.addAll(orderItemUpdatedList);
        itemsAdapter.notifyDataSetChanged();
        updateTotalBottom();
    }

    public void showSnackbar(){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Stok kosong, silahkan tambah stok terlebih dahulu", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void showSnackbarQty(){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Pesanan melebihi stok", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @SuppressLint("SetTextI18n")
    private void updateTotalBottom(){
        long totalQty = 0;
        long totalPrice = 0;
        transactionItems = new ArrayList<>();
        itemsList = new ArrayList<>();

        for (ItemVariants orditem:orederditems) {
            String uniqueId = UUID.randomUUID().toString();
            transactionItems.add(new TransactionPost.Items(orditem.getId()+"", orditem.getOrderQty(), 0, ""));
            itemsList.add(new TransactionToCashier.Items(uniqueId, orditem.getName(), orditem.getOrderQty(), orditem.getPrice(), "0", ""));
            totalQty += orditem.getOrderQty();
            long total_price = (long) (Integer.parseInt(orditem.getPrice()) * orditem.getOrderQty());
            totalPrice += total_price;
        }

        total_item.setText(totalQty + " x");
        total_price.setText("Rp " + MethodUtil.toCurrencyFormat(Long.toString(totalPrice)));
        total_price_overview.setText("Rp " + MethodUtil.toCurrencyFormat(Long.toString(totalPrice)));

        Gson gson = new Gson();
        listOrder = gson.toJson(orederditems);
        mTotalPrice = totalPrice;

        int maxLogSize = 1000;
        for(int i = 0; i <= listOrder.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = Math.min(end, listOrder.length());
            Log.v("orederditems: ", listOrder.substring(start, end));
        }
    }

    private void expandBottomView(boolean isExpanded){
        expanded = isExpanded;
        if (!expanded){
            expanded_view.setVisibility(View.GONE);
            collapsed_view.setVisibility(View.VISIBLE);
            overview_view.setVisibility(View.GONE);
        }
        else{
            expanded_view.setVisibility(View.VISIBLE);
            collapsed_view.setVisibility(View.GONE);
            overview_view.setVisibility(View.VISIBLE);
            overviewItemsAdapter.setDataList(orederditems);
            overviewItemsAdapter.notifyDataSetChanged();
            overviewListView.setHasMoreItems(false);

            Gson gson = new Gson();
            listOrder = gson.toJson(orderItemUpdatedList);

            int maxLogSize = 1000;
            for(int i = 0; i <= listOrder.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i+1) * maxLogSize;
                end = Math.min(end, listOrder.length());
                Log.v("orederditems 2: ", listOrder.substring(start, end));
            }

        }
    }

    private void expandBottomViewTo(boolean isExpanded){
        expanded = isExpanded;
        if (!expanded){
            expanded_view.setVisibility(View.GONE);
            collapsed_view.setVisibility(View.VISIBLE);
            overview_view.setVisibility(View.GONE);
            if (orederditems != null){
                Gson gson = new Gson();
                listOrder = gson.toJson(orederditems);
                Intent intent = new Intent(PickItemsActivity.this, PaymentActivity.class);
                Log.d("mTotal", String.valueOf(mTotalPrice));
                Log.d("listOrder", listOrder);
                String mTotalPrices = String.valueOf(mTotalPrice);
                intent.putExtra("transaction_type", transactionType);
                intent.putExtra("spesific_method", spesificMethod);
                intent.putExtra("total", mTotalPrices);
                intent.putExtra("json", listOrder);
                startActivity(intent);
            }
        }
        else{
            expanded_view.setVisibility(View.VISIBLE);
            collapsed_view.setVisibility(View.GONE);
            overview_view.setVisibility(View.VISIBLE);
            //the problem is here
            overviewItemsAdapter.setDataList(orederditems);
            overviewItemsAdapter.notifyDataSetChanged();
            overviewListView.setHasMoreItems(false);

            Gson gson = new Gson();
            listOrder = gson.toJson(orderItemUpdatedList);
//            System.out.println("orederditems 2: " + listOrder);

            int maxLogSize = 1000;
            for(int i = 0; i <= listOrder.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i+1) * maxLogSize;
                end = Math.min(end, listOrder.length());
                Log.v("orederditems 3: ", listOrder.substring(start, end));
            }
        }
    }

    private void loadTransactionsData(){
        currentPage = 0;
//        mPresenter.stockIteHms(loginStockLocation.location_id);
        mPresenter.newStockItems(loginStockLocation.location_id+"", loginStockLocation.location.getBrand().getId()+"", spesificItemTypeId);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPresenter.newStockItems(loginStockLocation.location_id+"", loginStockLocation.location.getBrand().getId()+"");
    }


    @Override
    public void showProgressLoading() {
        progressBar.show(this, "", false, null);
    }

    @Override
    public void hideProgresLoading() {
        progressBar.getDialog().dismiss();
    }

    @Override
    public NetworkService getService() {
        return NetworkManager.getInstance();
    }

    @Override
    public void onFailureRequest(String msg) {
        boolean isOfflineMode = PreferenceManager.isOfflineMode();
        if (isOfflineMode) {
            List<ItemVariants> items = PreferenceManager.getItemVariantList();
            itemsResponseList.addAll(items);

            item_name.setVisibility(View.GONE);
            imgSearch.setVisibility(View.GONE);
            laySearch.setVisibility(View.VISIBLE);
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<ItemVariants> newWorker = new ArrayList<>();
                    String newTextLowerCase = etSearch.getText().toString().toLowerCase();
                    for (ItemVariants response : itemsResponseList) {
                        if (response.getName().toLowerCase().contains(newTextLowerCase)) {
                            newWorker.add(response);
                        }
                    }
                    if (newWorker.size() >= 1){
                        itemsAdapter.setDataList(newWorker, context);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            itemsAdapter.setDataList(items, context);
            itemsListView.setHasMoreItems(false);
            itemsGridView.setHasMoreItems(false);
        } else {
            MethodUtil.showCustomToast(this, msg, R.drawable.ic_error_login);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressBar != null && progressBar.getDialog() != null) {
            progressBar.getDialog().dismiss();
        }
    }

    @Override
    public void onSuccessGetItems(List<Item> items) {
    }

    @Override
    public void onSuccessGetNewItems(List<ItemVariants> items) {
        itemsResponseList.addAll(items);
        PreferenceManager.setItemVariantsList(items);

        item_name.setVisibility(View.GONE);
        imgSearch.setVisibility(View.GONE);
        laySearch.setVisibility(View.VISIBLE);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<ItemVariants> newWorker = new ArrayList<>();
                String newTextLowerCase = etSearch.getText().toString().toLowerCase();
                for (ItemVariants response : itemsResponseList) {
                    if (response.getName().toLowerCase().contains(newTextLowerCase)) {
                        newWorker.add(response);
                    }
                }
                if (newWorker.size() >= 1){
                    itemsAdapter.setDataList(newWorker, context);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        itemsAdapter.setDataList(items, context);
        itemsListView.setHasMoreItems(false);
        itemsGridView.setHasMoreItems(false);
    }

    @Override
    public void onSuccessCreateTransaction(ResponseBody responseBody) {
        Log.d("TAG SUKSES", String.valueOf(responseBody));
        Intent intent = new Intent(PickItemsActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void itemVarianDeleted(ItemVariants itemVariants) {
        List<ItemVariants> itemVariantsList = orederditems;
        itemVariantsList.remove(itemVariants);

        overviewItemsAdapter.notifyDataSetChanged();
        overviewListView.setHasMoreItems(false);
        itemsAdapter.notifyDataSetChanged();
        updateTotalBottom();
    }

    @Override
    public void itemClicked(int position) {
        ItemVariants item = (ItemVariants) overviewItemsAdapter.itemList.get(position);
        int deletedPos = -1;
        int pos = 0;
        for (ItemVariants orditem:itemsAdapter.itemList) {
            if (orditem.getId().equals(item.getId())){
                deletedPos = pos;
                break;
            }
            pos++;
        }
        if (deletedPos != -1){
            itemsAdapter.itemList.get(pos).setOrderQty(0);
            itemsAdapter.notifyDataSetChanged();
            overviewItemsAdapter.itemList.remove(position);
            overviewItemsAdapter.notifyDataSetChanged();
        }
        updateTotalBottom();

    }

    @Override
    public void itemDeleted(int position) {
        ItemVariants item = itemsAdapter.itemList.get(position);
        int deletedPos = -1;
        int pos = 0;
        for (ItemVariants orditem:orederditems) {
            if (orditem.getId().equals(item.getId())){
                deletedPos = pos;
                orederditems.remove(orditem);
                break;
            }
            pos++;
        }
        if (deletedPos != -1){
//            overviewItemsAdapter.itemList.remove(pos);
            overviewItemsAdapter.notifyDataSetChanged();
            overviewListView.setHasMoreItems(false);
        }
        updateTotalBottom();
    }

    @Override
    public void itemAdd(int position) {
        ItemVariants item = (ItemVariants) itemsAdapter.itemList.get(position);
        int found = -1;
        int pos = 0;
        for (ItemVariants orditem:orederditems) {
            if (orditem.getId().equals(item.getId())){
                found = pos;
                break;
            }
            pos++;
        }
        if (found != -1){
            orederditems.get(pos).setOrderQty(item.getOrderQty());
            itemsAdapter.notifyDataSetChanged();
        }
        else{
            orederditems.add(item);
        }
        updateTotalBottom();
    }

    @Override
    public void itemMinus(int position) {
        ItemVariants item = (ItemVariants) itemsAdapter.itemList.get(position);
        int found = -1;
        int pos = 0;
        for (ItemVariants orditem:orederditems) {
            if (orditem.getId().equals(item.getId())){
                found = pos;
                break;
            }
            pos++;
        }
        if (found != -1){
            if (item.getOrderQty() == 0) {
                orederditems.remove(item);
            } else {
                orederditems.get(pos).setOrderQty(item.getOrderQty());
            }

            itemsAdapter.notifyDataSetChanged();
        }
        else{
            orederditems.add(item);
        }
        updateTotalBottom();
    }

    private void openOverviewView(){
        overviewItemsAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(overviewListView);

        overviewListView.setHasMoreItems(false);
    }
    void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @OnClick(R.id.imgClose)
    void onCLickClose2(){
        etSearch.setText("");
        item_name.setVisibility(View.VISIBLE);
        imgSearch.setVisibility(View.VISIBLE);
        laySearch.setVisibility(View.GONE);
    }
//
//    @OnClick(R.id.imgSearch)
//    void onClickSearch2(){
//        item_name.setVisibility(View.GONE);
//        imgSearch.setVisibility(View.GONE);
//        laySearch.setVisibility(View.VISIBLE);
//        etSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                List<ItemVariants> newWorker = new ArrayList<>();
//                String newTextLowerCase = etSearch.getText().toString().toLowerCase();
//                for (ItemVariants user : itemsResponseList) {
//                    if (user.getName().toLowerCase().contains(newTextLowerCase)) {
//                        newWorker.add(user);
//                    }
//                }
//                if (newWorker.size() >= 1){
//                    itemsAdapter.updateData(newWorker);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//    }
}
