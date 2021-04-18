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

    }

    @Override
    public int getItemCount() {
        return modelActivityDoneDrList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView sentTime, patientId,testName,text,confirmUser;

        public ViewHolder(View itemView) {
            super(itemView);

            sentTime = (TextView)itemView.findViewById(R.id.sentTime);
            patientId = (TextView)itemView.findViewById(R.id.patientId);
            testName = (TextView)itemView.findViewById(R.id.testName);
            text = (TextView)itemView.findViewById(R.id.messageText);
            confirmUser=(TextView)itemView.findViewById(R.id.confirmUser);
        }
    }
}
