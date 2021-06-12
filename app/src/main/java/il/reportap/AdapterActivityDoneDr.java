package il.reportap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.il.reportap.R;

import java.util.List;

;

public class AdapterActivityDoneDr extends RecyclerView.Adapter<AdapterActivityDoneDr.ViewHolder>{
    private List<ModelActivityDoneDr> modelActivityDoneDrList;
    private Context context;

    public AdapterActivityDoneDr(List<ModelActivityDoneDr> modelActivityDoneDrList, Context context) {
        this.modelActivityDoneDrList = modelActivityDoneDrList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_done,parent,false);
        return new ViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            ModelActivityDoneDr modelActivityDoneDr = modelActivityDoneDrList.get(position);

            holder.sentTime.setText(modelActivityDoneDr.getSentTime());
            holder.patientId.setText(modelActivityDoneDr.getPatientId());
            holder.testName.setText(modelActivityDoneDr.getTestName());
            holder.text.setText(modelActivityDoneDr.getText());
            holder.confirmUser.setText(modelActivityDoneDr.getConfirmUser());

            holder.messageID = modelActivityDoneDr.getId();
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
        return modelActivityDoneDrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView sentTime, patientId,testName,text,confirmUser;
        public Integer messageID;
        public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView)itemView.findViewById(R.id.card_view);
            sentTime = (TextView)itemView.findViewById(R.id.sentTime);
            patientId = (TextView)itemView.findViewById(R.id.patientId);
            testName = (TextView)itemView.findViewById(R.id.testName);
            text = (TextView)itemView.findViewById(R.id.messageText);
            confirmUser=(TextView)itemView.findViewById(R.id.confirmUser);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
