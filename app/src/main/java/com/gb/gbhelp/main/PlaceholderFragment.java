package com.gb.gbhelp.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gb.gbhelp.AppsAdapter;
import com.gb.gbhelp.CommunityAdapter;
import com.gb.gbhelp.CommunityModel;
import com.gb.gbhelp.FeedsAdapter;
import com.gb.gbhelp.FeedsModel;
import com.gb.gbhelp.GB;
import com.gb.gbhelp.MessageModel;
import com.gb.gbhelp.R;
import com.gb.gbhelp.databinding.FragmentMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private FragmentMainBinding binding;

    DatabaseReference databaseReferenceCommunity, databaseReferenceFeeds;
    CommunityAdapter communityAdapter;
    AppsAdapter appsAdapter;
    FeedsAdapter feedsAdapter;
    int index = 1;

    public PlaceholderFragment(int index) {
        this.index = index;
    }

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment(0);
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if (index == 0) {
            getFeedsFragment();
        } else if (index == 1) {
            getAppsFragment();
        } else if (index == 2) {
            getCommunityFragment();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getCommunityFragment() {
        communityAdapter = new CommunityAdapter(this.getContext());

        ListView appsList = binding.appsList;
        RecyclerView feedsList = binding.feedsList;
        RecyclerView communityList = binding.communityList;
        appsList.setVisibility(View.GONE);
        feedsList.setVisibility(View.GONE);
        communityList.setVisibility(View.VISIBLE);
        communityList.setAdapter(communityAdapter);
        communityList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        communityList.addItemDecoration(new DividerItemDecoration(communityList.getContext(), DividerItemDecoration.VERTICAL));

        databaseReferenceCommunity = FirebaseDatabase.getInstance().getReference(GB.DATABASE_COMMUNITY);

        databaseReferenceCommunity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashSet<CommunityModel> communityModelHashSet = new HashSet<>();
                ArrayList<CommunityModel> communityModelArrayList = new ArrayList<>();
                if (GB.isAdmin()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        GB.printLog("test2/" + snapshot.getChildrenCount() + "");
                        Object object = dataSnapshot.getKey();
                        GB.printLog("test2/" + object.toString());

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            MessageModel messageModel = dataSnapshot1.getValue(MessageModel.class);
                            if (messageModel.getSenderId().equals(GB.ADMIN_ID))
                                continue;
                            CommunityModel communityModel = new CommunityModel();
                            communityModel.setNickName(messageModel.getNickName());
                            communityModel.setSenderId(messageModel.getSenderId());
                            communityModel.setTag(messageModel.getTag());
                            GB.printLog(communityModelHashSet.toString());
                            communityModelHashSet.add(communityModel);
                        }
                    }

                    communityModelArrayList.addAll(communityModelHashSet);
                } else {

                    CommunityModel communityModel = new CommunityModel();
                    communityModel.setNickName(getString(R.string.my_bugs));
                    communityModel.setTag(GB.CHAT_TAG_HELP);
                    communityModel.setSenderId(GB.ADMIN_ID);
                    communityModelArrayList.add(communityModel);

                    communityModel = new CommunityModel();
                    communityModel.setNickName(getString(R.string.my_suggestions));
                    communityModel.setTag(GB.CHAT_TAG_SUGGESTION);
                    communityModel.setSenderId(GB.ADMIN_ID);
                    communityModelArrayList.add(communityModel);
                }
                communityAdapter.add(communityModelArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getAppsFragment() {

        ListView appsList = binding.appsList;
        RecyclerView feedsList = binding.feedsList;
        RecyclerView communityList = binding.communityList;
        appsList.setVisibility(View.VISIBLE);
        feedsList.setVisibility(View.GONE);
        communityList.setVisibility(View.GONE);

        appsAdapter = new AppsAdapter(this.getActivity());
        appsList.setAdapter(appsAdapter);
    }

    private void getFeedsFragment() {

        ListView appsList = binding.appsList;
        RecyclerView feedsList = binding.feedsList;
        RecyclerView communityList = binding.communityList;
        appsList.setVisibility(View.GONE);
        feedsList.setVisibility(View.VISIBLE);
        communityList.setVisibility(View.GONE);

        feedsAdapter = new FeedsAdapter(this.getActivity());
        feedsList.setAdapter(feedsAdapter);
        feedsList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        feedsList.addItemDecoration(new DividerItemDecoration(feedsList.getContext(), DividerItemDecoration.VERTICAL));

        databaseReferenceFeeds = FirebaseDatabase.getInstance().getReference(GB.DATABASE_FEEDS);

        databaseReferenceFeeds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashSet<FeedsModel> feedsModelHashSet = new HashSet<>();
                ArrayList<FeedsModel> feedsModelArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GB.printLog("test3/" + snapshot.getChildrenCount() + "");
                    Object object = dataSnapshot.getKey();
                    GB.printLog("test3/" + object.toString());
                    FeedsModel feedsModel1 = dataSnapshot.getValue(FeedsModel.class);

                    FeedsModel feedsModel = new FeedsModel();
                    feedsModel.setTitle(feedsModel1.getTitle());
                    feedsModel.setSummery(feedsModel1.getSummery());
                    feedsModel.setTitleAR(feedsModel1.getTitleAR());
                    feedsModel.setSummeryAR(feedsModel1.getSummeryAR());
                    feedsModel.setTimestamp(feedsModel1.getTimestamp());
                    feedsModel.setFileName(feedsModel1.getFileName());
                    feedsModel.setUrls(feedsModel1.getUrls());
                    feedsModel.setFileNameAR(feedsModel1.getFileNameAR());
                    feedsModel.setUrlsAR(feedsModel1.getUrlsAR());
                    feedsModel.setTag(feedsModel1.getTag());
                    GB.printLog("test3/" + feedsModel);
                    feedsModelHashSet.add(feedsModel);
                }

                feedsModelArrayList.addAll(feedsModelHashSet);
                feedsAdapter.add(feedsModelArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}