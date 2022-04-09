package ani.genshincoders.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ani.genshincoders.BottomSheetDialogFragment
import ani.genshincoders.databinding.BottomSheetDevelopersBinding

class DevelopersDialogFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetDevelopersBinding? = null
    private val binding get() = _binding!!

    private val developers = arrayOf(
        Developer("Ahmad Nabil  (A20DW2049)","https://yt3.ggpht.com/ytc/AKedOLSWUFCT09OlUZaXLCcnVB4dqCp_RSTmB0puAhVkYQ=s176-c-k-c0x00ffffff-no-rj","Owner & Maintainer","https://www.youtube.com/channel/UCNpUTnOHcrY5ekNQ06wjtmA"),
        Developer("Danesh   (A20DW2017)","https://yt3.ggpht.com/ytc/AKedOLQF-p0b6ZXYn11Ev-czQQQiaLrP2RIAlJZbXqp_uA=s176-c-k-c0x00ffffff-no-rj","Maintainer","https://www.youtube.com/channel/UC5pDrgWzhayaozspLQGw_yw/featured"),
        Developer("Hanif Azri   (A20DW0185)","https://yt3.ggpht.com/ytc/AKedOLR9CphV2W80JJ1Mjl4JP-c5ctJ94mi-LWTYHiUSnQ=s176-c-k-c0x00ffffff-no-rj","Contributor","https://www.youtube.com/c/NepzGamingOfficial"),
        Developer("Fahim Mirza  (A20DW2123)","https://yt3.ggpht.com/ytc/AKedOLQ63iCPuvu4iYLyfGQZFD6n34SIfaT8nIriOZ0qGw=s176-c-k-c0x00ffffff-no-rj","Designer","https://www.youtube.com/channel/UCL-viqibUaAX0RsRLhvl9iQ"),
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetDevelopersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.devsRecyclerView.adapter = DevelopersAdapter(developers)
        binding.devsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}