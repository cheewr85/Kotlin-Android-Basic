### Terminology
- Classes는 Object를 만들기 위한 설계도임(예를 들어 `Aquarium` 클래스는 `Aquarium` 객체를 만들기 위한 설계도임)

- Object는 Class의 instance임(`Aquarium` 객체는 실제 하나의 `Aquarium`임)

- Properties는 Class의 특성임(`Aquarium`의 길이, 높이, 넓이등)

- Method는 member function이라고도 부르는데, 클래스의 기능을 담당함, 즉 Object를 가지고 무엇을 할 지 쓴 것임(`Aquarium`객체의 `fillWithWater`메소드를 쓸 수 있음)

- Interface는 Class가 implement할 수 있는 specification임(예를 들어 `Aquarium`객체 말고도 cleaning하는 것은 비슷한 객체의 모든 공통사항인데 이를 `Clean`이라는 인터페이스를 만들어서 `clean`메소드를 정의할 수 있음, `Aquarium` 클래스는 `Clean`인터페이스를 implement하여서 원하는대로 정의가능)

- Package는 관련된 코드 그룹이 묶여서 구성된 그룹이나 라이브러리 코드를 만들기 위한 것임, 한 번 패키지가 만들어지면 다른 파일에서 이를 `import`해서 코드와 클래스를 재사용할 수 있음
