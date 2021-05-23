package il.reportap;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.il.reportap.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterActivityInboxLab extends RecyclerView.Adapter<AdapterActivityInboxLab.ViewHolder> {
    private List<ModelActivityInboxLab> modelActivityInboxLabList;
    private Context context;
    private Boolean successMarkRead;

    public AdapterActivityInboxLab(List<ModelActivityInboxLab> modelActivityInboxLabList, Context context) {
        this.modelActivityInboxLabList = modelActivityInboxLabList;
        this.context = context;
        this.successMarkRead = false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_inbox_lab, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ModelActivityInboxLab modelActivityInboxLab = modelActivityInboxLabList.get(position);

        holder.sentTime.setText(modelActivityInboxLab.getSentTime());
        holder.patientId.setText(modelActivityInboxLab.getPatientId());
        holder.testName.setText(modelActivityInboxLab.getTestName());
        holder.text.setText(modelActivityInboxLab.getText());
        holder.component.setText(modelActivityInboxLab.getComponent());
        holder.senderDetails.setText(String.format("%s | %s", modelActivityInboxLab.getFullName(), modelActivityInboxLab.getDept()));
        holder.measurement.setText(modelActivityInboxLab.getMeasurement());
        holder.responseID = modelActivityInboxLab.getId();

        //Begin defining mark-as-read button response
        if (modelActivityInboxLab.getWasRead())             // If the message was read (the row is in DoneLab page) -> hide the eye button
            holder.wasReadButton.setVisibility(View.GONE);
        else {                                              // If the message wasn't read (the row is in InboxLab page) -> prepare the eye button and confirmation box
            holder.wasReadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmationBox(holder);
                }
            });
            holder.confirmationBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    hideConfirmationBox(holder);
                }
            });
            holder.confirmRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideConfirmationBox(holder);
                    markAsRead(holder.responseID, SharedPrefManager.getInstance(context.getApplicationContext()).getUser().getEmployeeNumber(), holder);
                }
            });
            holder.cancelRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideConfirmationBox(holder);
                }
            });
        }
        //End defining mark-as-read button response

        if (modelActivityInboxLab.getIsValueBool() == 1) {
            holder.measurement.setVisibility(View.INVISIBLE);
            if (Float.valueOf(modelActivityInboxLab.getResultValue()) == 1) {
                holder.resultValue.setText(": חיובי");
            } else holder.resultValue.setText(": שלילי");
        } else {
            holder.resultValue.setText(Float.toString(modelActivityInboxLab.getResultValue()));
        }
    }

    @Override
    public int getItemCount() {
        return modelActivityInboxLabList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView sentTime, patientId, testName, text, measurement, component, resultValue, senderDetails;
        public Integer responseID;
        public ImageButton wasReadButton;
        public ConstraintLayout confirmationBox;
        public Button confirmRead, cancelRead;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            sentTime = (TextView) itemView.findViewById(R.id.sentTimeL);
            patientId = (TextView) itemView.findViewById(R.id.patientIdL);
            testName = (TextView) itemView.findViewById(R.id.testNameL);
            text = (TextView) itemView.findViewById(R.id.messageTextL);
            measurement = (TextView) itemView.findViewById(R.id.valueType);
            component = (TextView) itemView.findViewById(R.id.component);
            resultValue = (TextView) itemView.findViewById(R.id.resValue);
            senderDetails = (TextView) itemView.findViewById(R.id.senderdetails);
            wasReadButton = (ImageButton) itemView.findViewById(R.id.eyeCheckedIL);
            confirmationBox = itemView.findViewById(R.id.confirmationBox);
            confirmRead = itemView.findViewById(R.id.buttonConfirmMarkRead);
            cancelRead = itemView.findViewById(R.id.buttonCancelMarkRead);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }

    public void showConfirmationBox(ViewHolder viewHolder) {
        if (viewHolder.confirmationBox.getVisibility() == View.GONE)
            viewHolder.confirmationBox.setVisibility(View.VISIBLE);
        else
            viewHolder.confirmationBox.setVisibility(View.GONE);
    }

    public void hideConfirmationBox(ViewHolder viewHolder) {
        viewHolder.confirmationBox.setVisibility(View.GONE);
    }

    public void markAsRead(Integer messageID, String userID, ViewHolder viewHolder) {   // This method doesn't return a value (impossible to return inside an onClick() method) but changes the global variable successMarkRead according to success.
        successMarkRead = false;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_MARK_AS_READ, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    successMarkRead = !jsonObject.getBoolean("error");
                    if (!successMarkRead) {
                        Toast.makeText(context.getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show(); // Show a message whether the operation succeeded or the error message
                        if (jsonObject.getBoolean("alreadyMarked")) {    // If the message had already been marked by another user, the function will also mark success TRUE and disable the button to mark again
                            successMarkRead = true;
                        }
                    } else {
                        Toast.makeText(context.getApplicationContext(), "ההודעה אושרה בהצלחה!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } finally {
                    if (successMarkRead) {
                        AnimatorListenerAdapter afterSlideAway = new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {    // Refresh the inbox page after animation is done
                                super.onAnimationEnd(animation);
                                Intent intent = new Intent(context.getApplicationContext(), InboxLab.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        };
                        viewHolder.cardView.animate().translationX(viewHolder.cardView.getWidth()).alpha(0.0f).setListener(afterSlideAway); // Animate the message row out of screen bounds, then refresh inbox
                    }
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context.getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("messageID", messageID.toString());
                params.put("userID", userID);
                params.put("isResponse", "true");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
