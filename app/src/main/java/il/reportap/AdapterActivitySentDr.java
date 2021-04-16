package il.reportap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.R;

import java.util.List;

public class AdapterActivitySentDr extends RecyclerView.Adapter<AdapterActivitySentDr.ViewHolder>{
    private List<ModelActivitySentDr> modelActivitySentDrList;
    private Context context;

    public AdapterActivitySentDr(List<ModelActivitySentDr> modelActivitySentDrList, Context context) {
        this.modelActivitySentDrList = modelActivitySentDrList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_sent,parent,false);
        return new ViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            ModelActivitySentDr modelActivitySentDr = modelActivitySentDrList.get(position);

            holder.sentTime.setText(modelActivitySentDr.getSent_time());
            holder.patientId.setText(modelActivitySentDr.getPatient_id());
            holder.testName.setText(modelActivitySentDr.getTest_name());
            holder.text.setText(modelActivitySentDr.getText());
            holder.senderName.setText(modelActivitySentDr.getSender_name());
            if (!modelActivitySentDr.getConfirm_time().equals("0"))
            {
                holder.isChecked.setImageResource(R.drawable.eyecheck2_bmp);
            }
    }

    @Override
    public int getItemCount() {
        return modelActivitySentDrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView patientId, text, sentTime,senderName,testName;
        public ImageView isChecked;

        public ViewHolder(View itemView) {
            super(itemView);

            sentTime = (TextView)itemView.findViewById(R.id.sentTimeS);
            patientId = (TextView)itemView.findViewById(R.id.patientIdS);
            testName = (TextView)itemView.findViewById(R.id.testNameS);
            text = (TextView)itemView.findViewById(R.id.RepText);
            isChecked=(ImageView)itemView.findViewById(R.id.isChecked);
            senderName=(TextView)itemView.findViewById(R.id.senderName);
        }
    }
}
