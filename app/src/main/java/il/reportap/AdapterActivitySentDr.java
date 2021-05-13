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

            holder.sentTime.setText(modelActivitySentDr.getSentTime());
            holder.patientId.setText(modelActivitySentDr.getPatientId());
            holder.testName.setText(modelActivitySentDr.getTestName());
            holder.text.setText(modelActivitySentDr.getText());
            holder.senderName.setText(modelActivitySentDr.getSenderName());
            holder.patientName.setText(modelActivitySentDr.getPatientName());
            if (!modelActivitySentDr.getConfirmTime().equals("0"))
            {
                holder.isChecked.setImageResource(R.drawable.eyecheck2_bmp);
            }
    }

    @Override
    public int getItemCount() {
        return modelActivitySentDrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView patientId, text, sentTime,senderName,testName, patientName;
        public ImageView isChecked;

        public ViewHolder(View itemView) {
            super(itemView);

            sentTime = itemView.findViewById(R.id.sentTimeS);
            patientId = itemView.findViewById(R.id.patientIdS);
            testName = itemView.findViewById(R.id.testNameS);
            text = itemView.findViewById(R.id.RepText);
            isChecked= itemView.findViewById(R.id.isChecked);
            senderName= itemView.findViewById(R.id.senderName);
            patientName=itemView.findViewById(R.id.patientName);
        }
    }
}
