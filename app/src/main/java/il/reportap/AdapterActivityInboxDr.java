package il.reportap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.R;

import java.util.List;

public class AdapterActivityInboxDr extends RecyclerView.Adapter<AdapterActivityInboxDr.ViewHolder>{
    private List<ModelActivityInboxDr> modelActivityInboxDrList;
    private Context context;

    public AdapterActivityInboxDr(List<ModelActivityInboxDr> modelActivityInboxDrList, Context context) {
        this.modelActivityInboxDrList = modelActivityInboxDrList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_inbox,parent,false);
        return new ViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            ModelActivityInboxDr modelActivityInboxDr = modelActivityInboxDrList.get(position);

            holder.sentTime.setText(modelActivityInboxDr.getSentTime());
            holder.patientId.setText(modelActivityInboxDr.getPatientId());
            holder.testName.setText(modelActivityInboxDr.getTestName());
            if (Integer.valueOf(modelActivityInboxDr.getIsUrgent())==1) {
               holder.isUrgent.setImageResource(R.drawable.redexclamation_trans);
            }
            holder.messageID = modelActivityInboxDr.getId();
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewMessage.class);
                    intent.putExtra("MESSAGE_ID", holder.messageID);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
    }

    @Override
    public int getItemCount() {
        return modelActivityInboxDrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView sentTime, patientId,testName;
        public ImageView isUrgent;
        public Integer messageID;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView)itemView.findViewById(R.id.card_view);
            sentTime = (TextView)itemView.findViewById(R.id.sentTime);
            patientId = (TextView)itemView.findViewById(R.id.patientId);
            testName = (TextView)itemView.findViewById(R.id.testName);
            isUrgent = (ImageView) itemView.findViewById(R.id.isUrgent);
        }
    }
}
