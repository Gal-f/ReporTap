package il.reportap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
               holder.isUrgent.setColorFilter(ContextCompat.getColor(context, R.color.red),
                       PorterDuff.Mode.MULTIPLY);
            }
    }

    @Override
    public int getItemCount() {
        return modelActivityInboxDrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView sentTime, patientId,testName;
        public ImageView isUrgent;

        public ViewHolder(View itemView) {
            super(itemView);

            sentTime = (TextView)itemView.findViewById(R.id.sentTime);
            patientId = (TextView)itemView.findViewById(R.id.patientId);
            testName = (TextView)itemView.findViewById(R.id.testName);
            isUrgent = (ImageView) itemView.findViewById(R.id.isUrgent);
        }
    }
}
