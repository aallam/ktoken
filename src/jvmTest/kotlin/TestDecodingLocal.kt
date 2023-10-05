import com.aallam.kotoken.EncodingName
import com.aallam.kotoken.loader.LocalPbeLoader
import okio.FileSystem

class TestDecodingLocal : AbstractDecoding(
    encodingName = EncodingName.CL100K_BASE,
    loader = LocalPbeLoader(fileSystem = FileSystem.RESOURCES),
)
