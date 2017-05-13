package net.jmecn.lemur;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioSource.Status;
import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Action;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.DefaultRangedValueModel;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.ProgressBar;
import com.simsilica.lemur.RangedValueModel;
import com.simsilica.lemur.RollupPanel;
import com.simsilica.lemur.Slider;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedList;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.event.CursorEventControl;
import com.simsilica.lemur.event.DragHandler;
import com.simsilica.lemur.list.SelectionModel;
import com.simsilica.lemur.list.SelectionModel.SelectionMode;
import com.simsilica.lemur.style.Attributes;
import com.simsilica.lemur.style.BaseStyles;
import com.simsilica.lemur.style.ElementId;
import com.simsilica.lemur.style.Styles;

/**
 * 音乐播放器
 * 
 * @author yanmaoyuan
 *
 */
public class MusicPlayer extends SimpleApplication {

	/**
	 * 音源数据屏
	 */
	private AudioNode musicSource;
	private AudioData musicData;

	/**
	 * 播放器
	 */
	private Container main;// 主窗口
	private Label timeLbl;// 播放时间
	private RangedValueModel progressModel;// 播放进度
	private VersionedReference<Double> volume;// 音量
	
	// 播放列表
	private VersionedList<Music> list;
	private SelectionModel selectionModel;
	
	class Music {
		private String title;
		private AudioKey audioKey;
		
		public Music(String title, String asset) {
			this.title = title;
			this.audioKey = new AudioKey(asset, true, true);
		}
		
		public AudioKey getAudioKey() {
			return audioKey;
		}
		
		@Override
		public String toString() {
			return title;
		}
	}
	
	@Override
	public void simpleInitApp() {
		// 初始化Lemur GUI
		GuiGlobals.initialize(this);

		// 加载 'glass' 样式
		BaseStyles.loadGlassStyle();

		// 将'glass'设置为GUI默认样式
		GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

		initStyle();
		
		initGui();
		
		initAudio();
	}

	/**
	 * 主循环
	 */
	@Override
	public void simpleUpdate(float tpf) {
		if (musicSource != null) {

			if (volume.needsUpdate()) {
				double value = volume.get();
				listener.setVolume((float) value);
			}

			if (musicSource.getStatus() == Status.Playing) {
				updateTime();
			}
		}
	}

	/**
	 * 更新播放进度
	 */
	private void updateTime() {
		float musicLength = musicData.getDuration();
		float curTime = musicSource.getPlaybackTime();

		// 更新进度条
		progressModel.setPercent(curTime/musicLength);

		int minutesTotal = (int) (musicLength / 60);
		int secondsTotal = (int) (musicLength % 60);
		int minutesNow = (int) (curTime / 60);
		int secondsNow = (int) (curTime % 60);
		String txt = String.format("%02d:%02d-%02d:%02d", minutesNow, secondsNow, minutesTotal, secondsTotal);
		
		// 更新时间
		timeLbl.setText(txt);
	}

	/**
	 * 初始化播放器样式
	 */
	private void initStyle() {
		BitmapFont font = GuiGlobals.getInstance().loadFont("Interface/Fonts/Player/MusicPlayer.fnt");
		
		Styles style = GuiGlobals.getInstance().getStyles();

		/**
		 * 设置播放器按钮样式
		 */
		Attributes attributes = style.getSelector("button", "player");
		
		// 设置字体参数
		attributes.set("font", font);// BitmapFont字体
		attributes.set("fontSize", 14f);
		attributes.set("color", ColorRGBA.White);
		
		// 背景色
		attributes.set("background", new QuadBackgroundComponent(new ColorRGBA(0.2f, 0.3f, 0.4f, 1f)));
		
		// 设置按钮之间的距离
		attributes.set("insets", new Insets3f(2, 2, 2, 2));
		attributes.set("textHAlignment", HAlignment.Center);
		
		/**
		 * 设置标题样式
		 */
		attributes = style.getSelector("title", "glass");
		attributes.set("font", font);
		
		/**
		 * 设置所有Label样式
		 */
		attributes = style.getSelector("label", "glass");
		attributes.set("font", font);
		attributes.set("fontSize", 14f);
		attributes.set("color", ColorRGBA.White);
	}
	
	/**
	 * 初始化界面布局
	 */
	private void initGui() {
		main = new Container();
		guiNode.attachChild(main);

		/**
		 * 播放器标题
		 */
		Label titleLbl = new Label("音乐播放器", new ElementId("title"), "glass");
		main.addChild(titleLbl);

		/**
		 * 进度面板
		 */
		// 进度条，用于显示音乐播放进度
		progressModel = new DefaultRangedValueModel(0, 100, 0);

		ProgressBar progressBar = new ProgressBar(progressModel);
		progressBar.setPreferredSize(new Vector3f(200, 10, 1));

		// 用于显示音乐时间
		timeLbl = new Label("--:-- / --:--");
		timeLbl.setTextHAlignment(HAlignment.Center);

		// 布局
		Container progressPanel = new Container(new BoxLayout(Axis.X, FillMode.Even));
		progressPanel.addChild(progressBar);
		progressPanel.addChild(timeLbl);
		main.addChild(progressPanel);

		/**
		 * 控制面板
		 */
		// 用于控制播放的按钮
		Button btnRewind = new ActionButton(rewind, "player");
		Button btnStop = new ActionButton(stop, "player");
		Button btnPlay = new ActionButton(play, "player");
		Button btnFF = new ActionButton(next, "player");

		// 用于调节音量
		Label volumeLbl = new Label("音量:");
		volumeLbl.setTextHAlignment(HAlignment.Right);// 水平靠右对齐

		Slider volumeBar = new Slider("player");
		volumeBar.getModel().setMinimum(0.0);
		volumeBar.getModel().setMaximum(1.0);
		volumeBar.getModel().setValue(1.0);

		volume = volumeBar.getModel().createReference();

		// 布局
		Container controlPanel = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Even, FillMode.Even));
		controlPanel.addChild(btnRewind);
		controlPanel.addChild(btnStop);
		controlPanel.addChild(btnPlay);
		controlPanel.addChild(btnFF);
		controlPanel.addChild(volumeLbl);
		controlPanel.addChild(volumeBar);
		main.addChild(controlPanel);

		/**
		 * 歌曲列表
		 */
		final ListBox<Music> listBox = new ListBox<Music>();
		
		// 设置列表数据
		list = listBox.getModel();
		list.add(new Music("Nature", "Sound/Environment/Nature.ogg"));
		list.add(new Music("Ocean Waves", "Sound/Environment/Ocean Waves.ogg"));
		list.add(new Music("River", "Sound/Environment/River.ogg"));
		list.add(new Music("Bang", "Sound/Effects/Bang.wav"));
		list.add(new Music("Beep", "Sound/Effects/Beep.ogg"));
		list.add(new Music("Foot steps", "Sound/Effects/Foot steps.ogg"));
		list.add(new Music("Gun", "Sound/Effects/Gun.wav"));
		list.add(new Music("kick", "Sound/Effects/kick.wav"));
		
		// 选择模式
		selectionModel = listBox.getSelectionModel();
		selectionModel.setSelectionMode(SelectionMode.Single);// 单选模式
		selectionModel.setSelection(0);// 默认选中第一个
		
		// 布局
		RollupPanel musicListPanel = new RollupPanel("歌曲", "glass");
		musicListPanel.setContents(listBox);
		main.addChild(musicListPanel);
		
		/**
		 * 允许拖拽
		 */
		CursorEventControl.addListenersToSpatial(main, new DragHandler());

		/**
		 * 将播放器居中显示
		 */
		float width = cam.getWidth();
		float height = cam.getHeight();
		Vector3f size = main.getPreferredSize();

		main.setLocalTranslation((width - size.x) * 0.5f, (height + size.y) * 0.5f, -1);
	}

	// 播放/暂停
	private Action play = new Action("播放") {
		@Override
		public void execute(Button source) {

			if (list.size() < 1) {
				return;
			}
			
			// 判断播放列表中当前哪一首歌被选中
			Integer index = selectionModel.getSelection();
			if (index == null) {
				// 默认选中第一首歌。
				index = 0;
				selectionModel.setSelection(0);
			}
			Music music = list.get(index);
			
			// 加载并播放音频
			try {
				musicData = getAssetManager().loadAudio(music.getAudioKey());

				// 关闭正在播放音频
				if (musicSource != null) {
					musicSource.stop();
				}
				
				// 创建一个新的音源
				musicSource = new AudioNode(musicData, music.getAudioKey());
				musicSource.setPositional(false);
				
				// 开始播放
				musicSource.setPitch(1);
				musicSource.play();
				
				updateTime();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * 初始化音源
	 */
	private void initAudio() {

		try {
			// 加载音频数据
			AudioKey key = new AudioKey("Sound/Environment/Nature.ogg", true, true);
			musicData = getAssetManager().loadAudio(key);

			// 创造音源
			musicSource = new AudioNode(musicData, key);
			musicSource.setPositional(false);

			updateTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 停止播放
	private Action stop = new Action("停止") {
		@Override
		public void execute(Button source) {
			if (musicSource != null) {
				musicSource.setPitch(1);
				musicSource.stop();
				play.setName("播放");
				updateTime();
			}
		}
	};

	// 减速播放
	private Action rewind = new Action("慢速") {
		@Override
		public void execute(Button source) {
			if (musicSource != null && musicSource.getStatus() == Status.Playing) {
				musicSource.setPitch(0.5f);
			}
		}
	};

	// 快速播放
	private Action next = new Action("快进") {
		@Override
		public void execute(Button source) {
			if (musicSource != null && musicSource.getStatus() == Status.Playing) {
				musicSource.setPitch(2);
			}
		}
	};

	public static void main(String[] args) {
		new MusicPlayer().start();
	}

}
