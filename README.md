# SimplePlayer

동영상 재생 플레이어<br/>

## 개발 기간
2022.04.24 ~ 2022.5.24<br/>

## 주요 기술 스택
Kotlin, ExoPlayer<br/>

## 프로젝트 환경
Min sdk: 26 (Android 8)  
Target sdk: 31 (Android 12)  
Jvm target: 1.8  
Programming language: Kotlin  

## 프로젝트 기능
### 1. 폴더 리스트
<img height="600" width="270" src="https://user-images.githubusercontent.com/68229193/169881908-2e3be116-681f-4ea1-9d8e-ba0b60337f1d.gif"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <img height="600" width="270" src="https://user-images.githubusercontent.com/68229193/169882292-9bf540e2-9021-4b69-99a0-492cb19530a4.jpg"/>  
처음 앱 시작 시 Splashscreen 후 MediaStore을 통해 동영상 파일을 읽기 위해 READ_EXTERNAL_STORAGE 권한을 요청한다.  
권한을 부여받으면 폴더별 동영상 리스트를 출력한다.
폴더 통째로 재생 목록에 저장할 수 있다.

### 2. 최근 동영상 리스트
<img height="600" width="270" src="https://user-images.githubusercontent.com/68229193/169883642-ab6dba69-8f11-4bd9-9345-9340556f31b7.jpg"/>  
최근 재생한 동영상을 보여준다.<br/>
Room을 사용하여 로컬 데이터베이스에 동영상 재생 날짜를 저장한다.
최근 동영상을 목록에서 삭제할 수 있다.<br/>

### 3. 재생 목록 리스트
<img height="600" width="270" src="https://user-images.githubusercontent.com/68229193/170060351-b952deba-83f7-4b4a-bad8-3e298fee18ac.gif"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img  height="600" width="270" src="https://user-images.githubusercontent.com/68229193/169883799-72a04157-578b-4ea8-8c16-039519ee7f8c.jpg"/><br/>
원하는 동영상들을 재생 목록에 모을 수 있다.<br/>
Room을 사용하여 로컬 데이터베이스에 재생 목록과 재생 목록의 동영상 ID를 저장한다.<br/>
재생 목록의 동영상을 삭제할 수 있다. 재생 목록을 삭제하거나 이름을 변경할 수 있다.
### 4. 검색
<img height="600" width="270" src="https://user-images.githubusercontent.com/68229193/170061886-8122349f-b208-409b-aad8-8dc2bc986e12.gif"/>
현재 리스트 아이템 중 글자를 입력할 때마다 검색어에 해당하는 결과를 보여준다.<br/>

### 5. 동영상 재생
<img height="270" width="600" src="https://user-images.githubusercontent.com/68229193/170056379-1e93c22e-a16c-40f4-bea5-dc2ee36c8a72.jpg"/><br/><img height="600" width="270" src="https://user-images.githubusercontent.com/68229193/170054061-1b6e029d-e6f6-4c50-b2c0-033b771a445f.gif"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img height="600" width="270" src="https://user-images.githubusercontent.com/68229193/169889737-513b6836-7f43-45b9-8370-69ed1ad4c9e3.gif"/><br/>
클릭한 동영상을 ExoPlayer로 재생한다.<br/>
되감기, 빨리 감기, 이전 동영상, 다음 동영상 기능이 있다.<br/>
동영상 width, height 비율에 따라 자동으로 orientation을 전환한다.<br/>
설정에서 플레이어 컨트롤러 테마를 터치방식으로 설정하면 더블탭 시 되감기 또는 빨리 감기를 한다.<br/>
### 6. PIP 모드
<img height="600" width="270" src="https://user-images.githubusercontent.com/68229193/170029300-1b1fb280-d13a-49de-958d-ac4535d90937.gif"/><br/>
Home Button 또는 OverviewButton을 터치하면 PIP모드로 들어간다.<br/>
PIP 모드에서는 재생, 정지, 전체화면으로 전환, 종료 기능이 있다.<br/>
동영상을 재생하는 PlayerActivity의 launchMode를 singleTask로 설정했기 때문에 다른 동영상을 클릭 시 PIP 모드의 동영상 대신 클릭한 동영상을 재생한다.<br/>

### 7. 설정
<img height="600" width="270" src="https://user-images.githubusercontent.com/68229193/170077144-1c37c7fd-81cd-4a6e-95ca-ba6f928ff111.jpg"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img height="600" width="270" src="https://user-images.githubusercontent.com/68229193/170077137-d6e67994-e31e-4de2-ac39-e9bb5b0a304e.jpg"/><br/>
Datastore, Preference로 구현한 앱 설정<br/>
플레이어 컨트롤러 테마를 변경할 수 있다.<br/>
최근 동영상 목록, 재생 목록을 초기화할 수 있다.<br/>
오픈소스 라이선스를 볼 수 있다.<br/>

## 주요 라이브러리
* Coroutine
* viewbinding, view model, livedata
* Exoplayer, Glide
* Room
* Preference, Datastore
## 주요 트러블 슈팅
<details>
  <summary>낮은 빈도로 동영상 플레이어(PlayerActivity) 종료 시 UI 겹침 현상 발생</summary>
  
  * 증상: 동영상 재생 화면에서 백 버튼 클릭 시 동영상 리스트 UI와 폴더 리스트 UI가 겹쳐서 나온다.<br/>
  한 번 더 백 버튼 클릭 시 두 폴더 리스트 UI가 겹쳐서 나온다.<br/>
  
  * 원인: 동영상 재생 중 드물게 현재 PlayerActivity 이전 Activity인 ListActivity가 destroy 되고, VideoListFragment도 destroy 된다.<br/>
  ListActivity가 destroy 된 이유는 시스템이 ListActivity를 종료시킨 것으로 생각된다.<br/>
  그래서 PlayerActivity가 종료되면 ListActivity가 create 되면서 activity에 FolderListFragment를 추가하는 코드가 동작했기에 UI가 겹치는 문제가 발생한 것으로 생각된다.<br/>
  
  * 조치: 만약 ListActivity가 recreat시 기본 Fragment(FolderListFragment)를 추가 안 하게 조치했다.
  ```kt
    private var isDefault = true
    override fun onCreate(savedInstanceState: Bundle?) {
        ...
        if (savedInstanceState != null) {
            isDefault = savedInstanceState.getBoolean(isDefaultKey)
        }
        ...
        initUi()
    }
    private fun initUi() {
        if(isDefault) {
            isDefault = false
            addDefaultListFragment()
        }
        ...
    }
    private fun addDefaultListFragment() {
        setAppbarTitleText(R.string.appbar_title_folder)
        supportFragmentManager.beginTransaction().add(binding.recyclerViewContainer.id, FolderListFragment()).commit()
    }
     override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(isDefaultKey, isDefault)
    }
  ```
  
</details>

<details>
  <summary>가끔씩 최근 동영상이나 재생목록, 재생목록의 동영상 삭제 할 때나 재생목록의 이름 수정 시 리스트가 업데이트 안 되는 문제</summary>
  
  * 증상: 최근 동영상, 재생목록, 재생목록의 동영상 삭제 시 리스트에 삭제한 아이템이 사라져야지만 리스트에 남아있다.</br>
  재생목록의 이름 수정 시 수정 전 이름 그대로이다.</br>
  SwipeRefresh을 통해 업데이트 호출해야지 적용된 UI를 볼 수 있다.</br>
  
  * 원인: 삭제 또는 수정 함수 호출 후 리스트 업데이트 함수를 호출하는데 이 두 함수는 비동기로 작동하기 때문에 변경 전 데이터가 업데이트되는 경우가 있다. 
  
  * 조치: 리스트 업데이트 함수를 삭제하고, 리스트 업데이트를 Flow에 맡기기로 했다.
  
  재생목록 리스트의 경우</br>
  PlaylistViewModel.kt
  ```kt
  val playlistsWithVideoInfo = playlistRepository.getPlaylistsWithVideoInfo().asLiveData()
  ```
  PlaylistRepository.kt
  ```kt
  fun getPlaylistsWithVideoInfo() = playlistDbDao.getPlaylistsWithVideoInfo()
  ```
  PlaylistDbDao.kt
  ```kt
  @Transaction
  @Query("SELECT * FROM Playlist")
  fun getPlaylistsWithVideoInfo(): Flow<List<PlaylistWithVideoInfo>>
  ```
</details>
