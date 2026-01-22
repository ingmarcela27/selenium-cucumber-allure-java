@Navigation

Feature: Navigation bar
  To see the subpages
  Without logging in
  I can click the navigation bar links

  Background: I am on the Free Range Testers web without logging in.
    Given I navigate to FreeRangeTesters

  @Plans
  Scenario Outline: I can access the subpages through the navigation bar

    When I go to <section> using the navigation bar
    Examples:
      | section   |
      | Academia  |
      | Cursos    |
      | Mentorías |
      | Talleres  |
      | Blog      |
      | Recursos  |

  @Courses
  Scenario: Courses are presented correctly to potential customers
    When I go to Cursos using the navigation bar
    And I select Introduccion al Testing de Software

  @Plans
  Scenario: Users can select a plan when signing up
    When I go to Cursos using the navigation bar
    And I select Elegir Plan
    Then I am redirected to the checkout page
