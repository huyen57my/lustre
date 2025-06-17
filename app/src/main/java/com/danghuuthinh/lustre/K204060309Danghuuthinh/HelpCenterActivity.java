package com.danghuuthinh.lustre.K204060309Danghuuthinh;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danghuuthinh.lustre.Adapter.ContactUsAdapter;
import com.danghuuthinh.lustre.Adapter.HelpAdapter;
import com.danghuuthinh.lustre.R;
import com.danghuuthinh.lustre.models.ContactUsItem;
import com.danghuuthinh.lustre.models.HelpItem;

import java.util.ArrayList;
import java.util.List;

public class HelpCenterActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFAQ, recyclerViewContact;
    private HelpAdapter helpAdapter;
    private ContactUsAdapter contactAdapter;

    private List<HelpItem> allHelpItems = new ArrayList<>();
    private View underlineFAQ, underlineContactUs;
    private Button btnFAQ, btnContactUs;
    private View layoutFAQ, layoutContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);


        btnFAQ = findViewById(R.id.btnFAQ);
        btnContactUs = findViewById(R.id.btnContactUs);
        underlineFAQ = findViewById(R.id.underlineFAQ);
        underlineContactUs = findViewById(R.id.underlineContactUs);
        layoutFAQ = findViewById(R.id.layoutFAQ);
        layoutContact = findViewById(R.id.layoutContactUs);

        recyclerViewFAQ = findViewById(R.id.recyclerViewFAQ);
        recyclerViewFAQ.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFAQ.setVisibility(View.VISIBLE);

        recyclerViewContact = findViewById(R.id.recyclerViewContact);
        recyclerViewContact.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContact.setVisibility(View.GONE);

        // Dữ liệu FAQ
        allHelpItems.add(new HelpItem("How to reset password?", "Go to settings > Account > Reset Password.", "Account"));
        allHelpItems.add(new HelpItem("What is the refund policy?", "Refunds are processed within 7 days.", "Services"));
        allHelpItems.add(new HelpItem("How to contact support?", "Tap Contact Us from the Help Center.", "General"));
        allHelpItems.add(new HelpItem("Can I change my email address?", "Yes. Navigate to Account Settings > Email to update your address.", "Account"));
        allHelpItems.add(new HelpItem("Is there a delivery charge?", "Delivery is free for orders over $50. Otherwise, a flat rate of $5 applies.", "Services"));
        allHelpItems.add(new HelpItem("What payment methods are accepted?", "We accept credit cards, PayPal, and Apple Pay.", "General"));
        allHelpItems.add(new HelpItem("How do I delete my account?", "Please contact support to request account deletion.", "Account"));
        allHelpItems.add(new HelpItem("Can I track my order?", "Yes. Go to Orders > Track Order to view current status.", "Services"));

        helpAdapter = new HelpAdapter(allHelpItems);
        recyclerViewFAQ.setAdapter(helpAdapter);

        // Dữ liệu Contact Us
        List<ContactUsItem> contactItems = new ArrayList<>();
        contactItems.add(new ContactUsItem("Customer Service", "Call us at 123-456-7890", R.mipmap.ic_customer));
        contactItems.add(new ContactUsItem("Whatsapp", "Chat via Whatsapp: +84123456789", R.mipmap.ic_whatsapp));
        contactItems.add(new ContactUsItem("Website", "Visit: www.example.com", R.mipmap.ic_web));
        contactItems.add(new ContactUsItem("Facebook", "Facebook page: fb.com/example", R.mipmap.ic_facebook));
        contactItems.add(new ContactUsItem("X", "Follow us on X (Twitter)", R.mipmap.ic_x));
        contactItems.add(new ContactUsItem("Instagram", "IG: @example", R.mipmap.ic_ins));

        contactAdapter = new ContactUsAdapter(contactItems);
        recyclerViewContact.setAdapter(contactAdapter);

        btnContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFAQ.setTextColor(ContextCompat.getColor(HelpCenterActivity.this, R.color.gray));
                btnContactUs.setTextColor(ContextCompat.getColor(HelpCenterActivity.this, R.color.brown));
                underlineFAQ.setVisibility(View.INVISIBLE);
                underlineContactUs.setVisibility(View.VISIBLE);

                recyclerViewFAQ.setVisibility(View.GONE);
                recyclerViewContact.setVisibility(View.VISIBLE);
            }
        });

        btnFAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFAQ.setTextColor(ContextCompat.getColor(HelpCenterActivity.this, R.color.brown));
                btnContactUs.setTextColor(ContextCompat.getColor(HelpCenterActivity.this, R.color.gray));
                underlineFAQ.setVisibility(View.VISIBLE);
                underlineContactUs.setVisibility(View.INVISIBLE);

                recyclerViewFAQ.setVisibility(View.VISIBLE);
                recyclerViewContact.setVisibility(View.GONE);
            }
        });


        // Nút quay lại
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Các nút lọc danh mục
        findViewById(R.id.btnAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpAdapter.updateList(allHelpItems);
            }
        });

        findViewById(R.id.btnServices).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByCategory("Services");
            }
        });

        findViewById(R.id.btnGeneral).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByCategory("General");
            }
        });

        findViewById(R.id.btnAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterByCategory("Account");
            }
        });
    }

    private void filterByCategory(String category) {
        List<HelpItem> filtered = new ArrayList<>();
        for (HelpItem item : allHelpItems) {
            if (item.getCategory().equals(category)) {
                filtered.add(item);
            }
        }
        helpAdapter.updateList(filtered);
    }
}
